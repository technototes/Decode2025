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
import { AnonymousPathChain, IndexedPCFile } from './types';

type IndexedFile = {
  getValueNames(): string[];
  getPoseNames(): string[];
  getBezierNames(): string[];
  getPathChainNames(): string[];
  getValue(name: string): AnonymousValue | undefined;
  getPose(name: string): AnonymousPose | undefined;
  getBezier(name: string): AnonymousBezier | undefined;
  getPathChain(name: string): AnonymousPathChain | undefined;
  setValue(name: string, value: AnonymousValue): void;
  setPose(name: string, pose: AnonymousPose): void;
  setBezier(name: string, bezier: AnonymousBezier): void;
  setPathChain(name: string, pathChain: AnonymousPathChain): void;
};

export function MakeIndexedFile(pcf: PathChainFile): ErrorOr<IndexedFile> {
  let icf: IndexedPCFile = {
    values: new Map<string, AnonymousValue>(),
    poses: new Map<string, AnonymousPose>(),
    beziers: new Map<string, AnonymousBezier>(),
    pathChains: new Map<string, AnonymousPathChain>(),
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

export function getValue(vr: ValueRef): number {
  return numFromVal(
    isRef(vr) ? ipcf.values[ipcf.namedValues.get(vr)].value : vr,
  );
}

export function pointFromPose(pr: AnonymousPose): Point {
  return { x: getValue(pr.x), y: getValue(pr.y) };
}

export function getPose(pr: PoseRef): AnonymousPose {
  try {
    return isRef(pr) ? ipcf.poses[ipcf.namedPoses.get(pr)].pose : pr;
  } catch (e) {
    // console.error(`Invalid PoseRef ${pr}`);
    throw e;
  }
}

export function pointFromPoseRef(pr: PoseRef): [number, Point] {
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
