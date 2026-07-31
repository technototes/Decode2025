import {
  AccError,
  ErrorOr,
  isDefined,
  isError,
  isString,
  isUndefined,
  MakeError,
} from '@freik/typechk';

import {
  AnonymousBezier,
  AnonymousFacing,
  AnonymousPose,
  BezierName,
  BezierRef,
  BezierType,
  EmptyParsedClass,
  FacingPiece,
  FacingType,
  HeadingRef,
  isRadiansRef,
  isRef,
  ParsedClass,
  PathChainName,
  PoseName,
  PoseRef,
  RadiansRef,
  ValueName,
  ValueRef,
} from '../../server/types';
import { readConstant } from '../ExpressionEval';
import { AnonymousPathChain, NameLookup, OneFileIndex } from '../types';
import { ValidRes } from './API';

export function MakeFileIndex(container: ParsedClass): OneFileIndex {
  const namedValues = new Map<ValueName, ValueRef | RadiansRef>(
    container.values.map((nv) => [nv.name, nv.value]),
  );
  const namedPoses = new Map<PoseName, PoseRef>(
    container.poses.map((np) => [np.name, np.pose]),
  );
  const namedBeziers = new Map<BezierName, BezierRef>(
    container.beziers.map((nb) => [nb.name, nb.points]),
  );
  const namedPathChains = new Map<PathChainName, AnonymousPathChain>(
    container.pathChains.map((npc) => [
      npc.name,
      { paths: npc.paths, heading: npc.pathHeading },
    ]),
  );
  const staticShortcuts = new Map<string, string>(
    container.pathChainHelpers.map((pch) => [pch.name, pch.staticType]),
  );
  return {
    container,
    namedValues,
    namedBeziers,
    namedPoses,
    namedPathChains,
    staticShortcuts,
  };
}

// Make a thing that can accumulate indexes (*can* in the *future*)
function MakeNameLookup(): NameLookup {
  let indexMap: Map<string, OneFileIndex> = new Map();
  const registerIndex = (index: OneFileIndex) => {
    indexMap.set(index.container.fullName, index);
  };
  // Private helper to find an index for a given full name (or parsed class)
  const getIndex = (pc: ParsedClass | string): OneFileIndex | undefined => {
    return indexMap.get(isString(pc) ? pc : pc.fullName);
  };
  // Private helper to look for names, including cross-class and static namespace shortcuts
  function dig<K extends string, V>(
    val: K,
    context: ParsedClass,
    sel: (idx: OneFileIndex) => Map<K, V>,
  ): V | undefined {
    const index = getIndex(context);
    if (isUndefined(index)) {
      console.error('Unable to find context index', context.fullName);
      return;
    }
    const res = sel(index).get(val);
    if (isDefined(res)) {
      return res;
    }
    // We didn't find the item. If it has no dots, then it's a flat name, and we're done.
    const dot = val.indexOf('.');
    if (dot < 0) {
      return;
    }
    const ss = getIndex(context)?.staticShortcuts.get(val.substring(0, dot));
    if (isDefined(ss)) {
      // Try again, with the updated name:
      return dig(`${ss}${val.substring(dot)}`, context, sel);
    }
    // Okay, let's see if we can find a package class that matches
    const lastDot = val.lastIndexOf('.');
    const prefix = val.substring(0, lastDot);
    const suffix = val.substring(lastDot + 1);
    // Walk the import list, looking for a fully qualified class name
    // that exists in the index
    for (const imp of context.imports) {
      const pkg = getIndex(`${imp}.${prefix}`);
      if (isUndefined(pkg)) {
        continue;
      }
      const maybe = dig(suffix, pkg.container, sel);
      if (isDefined(maybe)) {
        return maybe;
      }
    }
  }
  const findValue = (
    val: ValueName,
    context: ParsedClass,
  ): ValueRef | RadiansRef | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedValues);
  };
  const findPose = (
    val: PoseName,
    context: ParsedClass,
  ): PoseRef | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedPoses);
  };
  const findBezier = (
    val: BezierName,
    context: ParsedClass,
  ): BezierRef | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedBeziers);
  };
  const findPath = (
    val: PathChainName,
    context: ParsedClass,
  ): AnonymousPathChain | undefined => {
    return dig(val, context, (idx: OneFileIndex) => idx.namedPathChains);
  };
  const reset = () => {
    indexMap.clear();
  };
  return { registerIndex, findBezier, findPath, findPose, findValue, reset };
}

const nameLookup = MakeNameLookup();
export function GetNameLookup(): NameLookup {
  return nameLookup;
}

export function ValidateIndex(
  fileIndex: OneFileIndex,
  lkup: NameLookup,
  context: ParsedClass,
): ErrorOr<true> {
  function checkValueRef(vr: ValueRef, id: string): ValidRes {
    if (isRef(vr)) {
      if (!lkup.findValue(vr, context)) {
        if (isUndefined(readConstant(vr))) {
          return MakeError(
            `${id}'s "${vr}" value reference appears to be undefined.`,
          );
        }
      }
    }
    return true;
  }

  function checkHeadingRef(hr: HeadingRef, id: string): ValidRes {
    if (isRadiansRef(hr)) {
      hr = hr.radians;
    }
    const valueRefCheck = checkValueRef(hr as ValueRef, id);
    if (valueRefCheck !== true) {
      // A heading ref could be a pose ref instead of a value ref
      // TODO: Maybe keep track of this stuff somehow?
      return checkPoseRef(hr as PoseName, id);
    }
    return true;
  }

  function checkAnonymousPose(pose: AnonymousPose, id: string): ValidRes {
    let res: ValidRes = true;
    if (pose.heading) {
      res = checkHeadingRef(pose.heading, `${id}'s heading`);
    }
    res = AccError(checkValueRef(pose.x, `${id}'s x coordinate`), res);
    return AccError(checkValueRef(pose.y, `${id}'s y coordinate`), res);
  }

  function checkPoseRef(pr: PoseRef, id: string): ValidRes {
    if (isRef(pr)) {
      return lkup.findPose(pr, context)
        ? true
        : // TODO: This should trigger cross file lookup
          MakeError(`${id}'s "${pr}" pose reference appears to be undefined`);
    }
    return checkAnonymousPose(pr, id);
  }

  function checkAnonymousBezier(curve: AnonymousBezier, id: string): ValidRes {
    let res: ValidRes = true;
    curve.points.forEach((pr, index) => {
      res = AccError(checkPoseRef(pr, `${id}'s element ${index}`), res);
    });
    if (curve.type === BezierType.Line && curve.points.length !== 2) {
      return AccError(res, MakeError(`${id}'s line doesn't have 2 points`));
    } else if (curve.type === BezierType.Curve && curve.points.length < 2) {
      return AccError(
        res,
        MakeError(`${id}'s line doesn't have enough points`),
      );
    }
    return res;
  }

  function checkBezierRef(br: BezierRef, id: string): ValidRes {
    if (isRef(br)) {
      return lkup.findBezier(br, context)
        ? true
        : // TODO: This should trigger cross file lookup
          MakeError(`${id}'s bezier reference appears to be undefined`);
    }
    return checkAnonymousBezier(br, id);
  }

  function checkAnonymousPathChain(
    apc: AnonymousPathChain,
    id: string,
  ): ValidRes {
    let res = validateFacing(apc.heading, id);
    apc.paths.forEach((br, index) => {
      res = AccError(checkBezierRef(br, `${id}'s path element ${index}`), res);
    });
    return res;
  }

  function validatePieces(pieces: FacingPiece[], id: string): ValidRes {
    let res: ValidRes = true;
    pieces.forEach((piece, idx) => {
      res = AccError(
        checkValueRef(piece.timing.start, `${id}'s timing start #${idx}`),
        res,
      );
      res = AccError(
        checkValueRef(piece.timing.end, `${id}'s timing end #${idx}`),
        res,
      );
      res = AccError(
        validateFacing(piece.heading, `${id}'s heading #${idx}`),
        res,
      );
    });
    return res;
  }

  function validateFacing(heading: AnonymousFacing, id: string): ValidRes {
    let res: ValidRes = true;
    switch (heading.type) {
      case FacingType.Constant:
        res = checkHeadingRef(heading.heading, `${id}'s constant heading ref`);
        break;
      case FacingType.Linear:
        res = checkHeadingRef(heading.start, `${id}'s start heading ref`);
        res = AccError(
          checkHeadingRef(heading.end, `${id}'s end heading ref`),
          res,
        );
        break;
      case FacingType.Tangent:
        break; // Nothing to see here...
      case FacingType.Reversed:
        res = validateFacing(
          heading.facing,
          `${id}'s reversed heading interpolator`,
        );
        break;
      case FacingType.Point:
        res = checkPoseRef(heading.point, `${id}'s point heading interpolator`);
        break;
      case FacingType.Piecewise:
        res = validatePieces(
          heading.pieces,
          `${id}'s Piecewise heading interpolator`,
        );
        break;
      default:
        console.error('NYI: Unknown heading interpolator type', heading);
    }
    return res;
  }

  function validateUniqueNames(): ValidRes {
    const allNames = new Set<string>([
      ...fileIndex.namedValues.keys(),
      ...fileIndex.namedPoses.keys(),
      ...fileIndex.namedBeziers.keys(),
      ...fileIndex.namedPathChains.keys(),
    ]);
    if (
      allNames.size !==
      fileIndex.namedValues.size +
        fileIndex.namedPoses.size +
        fileIndex.namedBeziers.size +
        fileIndex.namedPathChains.size
    ) {
      // TODO: Provide a detailed diagnostic of which names are duplicated
      return MakeError(
        'Duplicate names found between values, points, beziers, and path chains.',
      );
    }
    return true;
  }

  function validatePathChainIndex(): ErrorOr<true> {
    let good: ValidRes = true;
    fileIndex.namedPoses.forEach((pr, name) => {
      good = AccError(checkPoseRef(pr, name), good);
    });
    fileIndex.namedBeziers.forEach((br, name) => {
      good = AccError(checkBezierRef(br, name), good);
    });
    fileIndex.namedPathChains.forEach((apc, name) => {
      good = AccError(checkAnonymousPathChain(apc, name), good);
    });
    good = AccError(validateUniqueNames(), good);
    return isError(good) ? good : true;
  }

  const res = validatePathChainIndex();
  if (isError(res)) {
    return res;
  }
  return true;
}

export const EmptyMappedFile: OneFileIndex = {
  container: EmptyParsedClass,
  namedValues: new Map(),
  namedPoses: new Map(),
  namedBeziers: new Map(),
  namedPathChains: new Map(),
  staticShortcuts: new Map(),
};
