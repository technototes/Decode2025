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
import { AnonymousPathChain, IndexedFile, Point } from './types';

export function MakeIndexedFile(pcf: PathChainFile): ErrorOr<IndexedFile> {
  const values = new Map<string, AnonymousValue>(
    pcf.values.map((nv) => [nv.name, nv.value]),
  );
  const namedValues = [...pcf.values];
  const poses = new Map<string, AnonymousPose>(
    pcf.poses.map((np) => [np.name, np.pose]),
  );
  const namedPoses = [...pcf.poses];
  const beziers = new Map<string, AnonymousBezier>(
    pcf.beziers.map((nb) => [nb.name, nb.points]),
  );
  const namedBeziers = [...pcf.beziers];
  const pathChains = new Map<string, AnonymousPathChain>(
    pcf.pathChains.map((npc) => [
      npc.name,
      { paths: npc.paths, heading: npc.heading },
    ]),
  );
  const namedPathChains = [...pcf.pathChains];

  function checkValueRef(vr: ValueRef, id: string): ValidRes {
    if (isRef(vr)) {
      if (!values.has(vr) && !poses.has(vr)) {
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
      return poses.has(pr)
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
      return beziers.has(br)
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
      ...values.keys(),
      ...poses.keys(),
      ...beziers.keys(),
      ...pathChains.keys(),
    ]);
    if (
      allNames.size !==
      values.size + poses.size + beziers.size + pathChains.size
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
    poses.forEach((pr, name) => {
      good = accError(checkPoseRef(pr, name), good);
    });
    beziers.forEach((br, name) => {
      good = accError(checkBezierRef(br, name), good);
    });
    pathChains.forEach((apc, name) => {
      good = accError(checkAnonymousPathChain(apc, name), good);
    });
    good = accError(validateUniqueNames(), good);
    return isError(good) ? good : true;
  }

  const res = validatePathChainIndex();
  if (isError(res)) {
    return res;
  }

  function getValueRefValue(vr: ValueRef): number {
    const av = isRef(vr) ? values.get(vr) : vr;
    if (isUndefined(av)) {
      throw new Error(`Invalid ValueRef ${vr}`);
    }
    return numFromVal(av);
  }

  function getPoseRefPoint(pr: PoseRef): Point {
    let ap: AnonymousPose = isRef(pr) ? poses.get(pr) : pr;
    try {
      return { x: getValueRefValue(ap.x), y: getValueRefValue(ap.y) };
    } catch (e) {
      throw new Error(`${e} from invalid PoseRef ${pr}`);
    }
  }

  function getBezierRefPoints(br: BezierRef): Point[] {
    const ab: AnonymousBezier = isRef(br) ? beziers.get(br) : br;
    return ab.points.map(getPoseRefPoint);
  }

  function getHeadingRefValue(hr: HeadingRef): number {
    if (isRef(hr)) {
      return getValueRefValue(hr);
    } else if (chkRadiansRef(hr)) {
      return (Math.PI * getValueRefValue(hr.radians)) / 180.0;
    } else {
      return getValueRefValue(hr);
    }
  }

  return {
    getValueNames(): string[] {
      return Array.from(values.keys());
    },
    getPoseNames(): string[] {
      return Array.from(poses.keys());
    },
    getBezierNames(): string[] {
      return Array.from(beziers.keys());
    },
    getPathChainNames(): string[] {
      return Array.from(pathChains.keys());
    },
    getValue(name: string): AnonymousValue | undefined {
      return values.get(name);
    },
    getPose(name: string): AnonymousPose | undefined {
      return poses.get(name);
    },
    getBezier(name: string): AnonymousBezier | undefined {
      return beziers.get(name);
    },
    getPathChain(name: string): AnonymousPathChain | undefined {
      return pathChains.get(name);
    },
    setValue(name: string, value: AnonymousValue): void {
      values.set(name, value);
    },
    setPose(name: string, pose: AnonymousPose): void {
      poses.set(name, pose);
    },
    setBezier(name: string, bezier: AnonymousBezier): void {
      beziers.set(name, bezier);
    },
    setPathChain(name: string, pathChain: AnonymousPathChain): void {
      pathChains.set(name, pathChain);
    },
    getValueRefValue,
    getPoseRefPoint,
    getBezierRefPoints,
    getHeadingRefValue,

    getValues: () => namedValues,
    getPoses: () => namedPoses,
    getBeziers: () => namedBeziers,
    getPathChains: () => namedPathChains,
    dump: () => {
      return (
        pcf.values.length +
        ' values, ' +
        pcf.poses.length +
        ' poses, ' +
        pcf.beziers.length +
        ' beziers, ' +
        pcf.pathChains.length +
        ' pathChains.'
      );
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
