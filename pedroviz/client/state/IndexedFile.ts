import { isUndefined } from '@freik/typechk';
import {
  accError,
  AnonymousBezier,
  AnonymousPose,
  AnonymousValue,
  BezierRef,
  chkConstantHeading,
  chkInterpolatedHeading,
  chkRadiansRef,
  ErrorOr,
  HeadingRef,
  isError,
  isRef,
  makeError,
  PathChainFile,
  PoseRef,
  ValueRef,
} from '../../server/types';
import { ValidRes } from './API';
import { AnonymousPathChain, IndexedFile, IndexedPCFile, Point } from './types';

export function MakeIndexedFile(pcf: PathChainFile): ErrorOr<IndexedFile> {
  let icf: IndexedPCFile = {
    values: new Map<string, AnonymousValue>(
      pcf.values.map((nv) => [nv.name, nv.value]),
    ),
    poses: new Map<string, AnonymousPose>(
      pcf.poses.map((np) => [np.name, np.pose]),
    ),
    beziers: new Map<string, AnonymousBezier>(
      pcf.beziers.map((nb) => [nb.name, nb.points]),
    ),
    pathChains: new Map<string, AnonymousPathChain>(
      pcf.pathChains.map((npc) => [
        npc.name,
        { paths: npc.paths, heading: npc.heading },
      ]),
    ),
  };

  function checkValueRef(vr: ValueRef, id: string): ValidRes {
    if (isRef(vr)) {
      if (!icf.values.has(vr) && !icf.poses.has(vr)) {
        return makeError(
          `${id}'s "${vr}" value reference appears to be undefined.`,
        );
      }
    }
    return true;
  }

  function checkHeadingRef(hr: HeadingRef, id: string): ValidRes {
    if (chkRadiansRef(hr)) {
      return checkValueRef(hr.radians, `${id}'s Radians ref`);
    }
    return checkValueRef(hr, id);
  }

  function checkAnonymousPose(pose: AnonymousPose, id: string): ValidRes {
    let res: ValidRes = true;
    if (pose.heading) {
      res = checkHeadingRef(pose.heading, `${id}'s heading`);
    }
    res = accError(checkValueRef(pose.x, `${id}'s x coordinate`), res);
    return accError(checkValueRef(pose.y, `${id}'s y coordinate`), res);
  }
  function checkPoseRef(pr: PoseRef, id: string): ValidRes {
    if (isRef(pr)) {
      return icf.poses.has(pr)
        ? true
        : makeError(`${id}'s "${pr}" pose reference appears to be undefined`);
    }
    return checkAnonymousPose(pr, id);
  }

  function checkAnonymousBezier(curve: AnonymousBezier, id: string): ValidRes {
    let res: ValidRes = true;
    curve.points.forEach((pr, index) => {
      res = accError(checkPoseRef(pr, `${id}'s element ${index}`), res);
    });
    if (curve.type === 'line' && curve.points.length !== 2) {
      return accError(res, makeError(`${id}'s line doesn't have 2 points`));
    } else if (curve.type === 'curve' && curve.points.length < 2) {
      return accError(
        res,
        makeError(`${id}'s line doesn't have enough points`),
      );
    }
    return res;
  }

  function checkBezierRef(br: BezierRef, id: string): ValidRes {
    if (isRef(br)) {
      return icf.beziers.has(br)
        ? true
        : makeError(`${id}'s bezier reference appears to be undefined`);
    }
    return checkAnonymousBezier(br, id);
  }
  function checkAnonymousPathChain(
    apc: AnonymousPathChain,
    id: string,
  ): ValidRes {
    let res: ValidRes = true;
    if (chkConstantHeading(apc.heading)) {
      res = checkHeadingRef(
        apc.heading.heading,
        `${id}'s constant heading ref`,
      );
    } else if (chkInterpolatedHeading(apc.heading)) {
      res = checkHeadingRef(
        apc.heading.headings[0],
        `${id}'s start heading ref`,
      );
      res = accError(
        checkHeadingRef(apc.heading.headings[1], `${id}'s end heading ref`),
        res,
      );
    }
    apc.paths.forEach((br, index) => {
      res = accError(checkBezierRef(br, `${id}'s path element ${index}`), res);
    });
    return res;
  }

  function validateUniqueNames(): ValidRes {
    const allNames = new Set<string>([
      ...icf.values.keys(),
      ...icf.poses.keys(),
      ...icf.beziers.keys(),
      ...icf.pathChains.keys(),
    ]);
    if (
      allNames.size !==
      icf.values.size + icf.poses.size + icf.beziers.size + icf.pathChains.size
    ) {
      // TODO: Provide a detailed diagnostic of which names are duplicated
      return makeError(
        'Duplicate names found between values, points, beziers, and path chains.',
      );
    }
    return true;
  }

  function validatePathChainIndex(): ErrorOr<true> {
    let good: ValidRes = true;
    icf.poses.forEach((pr, name) => {
      good = accError(checkPoseRef(pr, name), good);
    });
    icf.beziers.forEach((br, name) => {
      good = accError(checkBezierRef(br, name), good);
    });
    icf.pathChains.forEach((apc, name) => {
      good = accError(checkAnonymousPathChain(apc, name), good);
    });
    good = accError(validateUniqueNames(), good);
    return isError(good) ? good : true;
  }

  const res = validatePathChainIndex();
  if (isError(res)) {
    return res;
  }

  return {
    getValueNames(): string[] {
      return Array.from(icf.values.keys());
    },
    getPoseNames(): string[] {
      return Array.from(icf.poses.keys());
    },
    getBezierNames(): string[] {
      return Array.from(icf.beziers.keys());
    },
    getPathChainNames(): string[] {
      return Array.from(icf.pathChains.keys());
    },
    getValue(name: string): AnonymousValue | undefined {
      return icf.values.get(name);
    },
    getPose(name: string): AnonymousPose | undefined {
      return icf.poses.get(name);
    },
    getBezier(name: string): AnonymousBezier | undefined {
      return icf.beziers.get(name);
    },
    getPathChain(name: string): AnonymousPathChain | undefined {
      return icf.pathChains.get(name);
    },
    setValue(name: string, value: AnonymousValue): void {
      icf.values.set(name, value);
    },
    setPose(name: string, pose: AnonymousPose): void {
      icf.poses.set(name, pose);
    },
    setBezier(name: string, bezier: AnonymousBezier): void {
      icf.beziers.set(name, bezier);
    },
    setPathChain(name: string, pathChain: AnonymousPathChain): void {
      icf.pathChains.set(name, pathChain);
    },
  };
}

// Evaluation from the parsed code representation:
export function numFromVal(av: AnonymousValue): number {
  switch (av.type) {
    case 'double':
    case 'int':
      return av.value;
    case 'radians':
      return (Math.PI * av.value) / 180.0;
  }
}

export function getValue(ipcf: IndexedFile, vr: ValueRef): number {
  const av = isRef(vr) ? ipcf.getValue(vr) : vr;
  if (isUndefined(av)) {
    throw new Error(`Invalid ValueRef ${vr}`);
  }
  return numFromVal(av);
}

export function pointFromPose(ipcf: IndexedFile, pr: AnonymousPose): Point {
  return { x: getValue(ipcf, pr.x), y: getValue(ipcf, pr.y) };
}

export function getPose(ipcf: IndexedFile, pr: PoseRef): AnonymousPose {
  try {
    return isRef(pr) ? ipcf.getPose(pr) : pr;
  } catch (e) {
    throw new Error(`${e} from invalid PoseRef ${pr}`);
  }
}

export function pointFromPoseRef(
  ipcf: IndexedFile,
  pr: PoseRef,
): [number, Point] {
  return [getColorFor(getPose(pr)), pointFromPose(getPose(pr))];
}

export function getBezier(br: BezierRef): AnonymousBezier {
  return isRef(br) ? ipcf.beziers[ipcf.namedBeziers.get(br)].points : br;
}

export function getBezierPoints(br: BezierRef): [number, [number, Point][]] {
  const ab = getBezier(br);
  return [getColorFor(ab), ab.points.map(pointFromPoseRef)];
}

export function getValueFromHeaderRef(hr: HeadingRef): number {
  if (isRef(hr)) {
    return getValue(hr);
  } else if (chkRadiansRef(hr)) {
    return (Math.PI * getValue(hr.radians)) / 180.0;
  } else {
    return getValue(hr);
  }
}
