import { ReactElement, useCallback, useEffect, useRef, useState } from 'react';
import { useAtomValue } from 'node_modules/jotai/esm/react.mjs';

import { FieldVizPercentAtom, ThemeAtom } from 'client/state/SavedSettings';
import { FieldConfigHashAtom } from 'client/state/UserCode';
import { isNull, isUndefined } from 'node_modules/@freik/typechk/lib/esm';

import { ResponsiveAnchor, ResponsiveSquareCanvasProps } from '../types';

const defaultAnchor: ResponsiveAnchor = { x: 'center', y: 'middle' };

function translateX(x: 'left' | 'center' | 'right') {
  switch (x) {
    case 'left':
      return 'start';
    case 'center':
      return 'center';
    case 'right':
      return 'end';
  }
}

function translateY(y: 'top' | 'middle' | 'bottom') {
  switch (y) {
    case 'top':
      return 'start';
    case 'middle':
      return 'middle';
    case 'bottom':
      return 'end;';
  }
}

function getObjectPos(anchor: ResponsiveAnchor): string {
  const x = anchor.x === 'center' ? '50%' : anchor.x;
  const y = anchor.y === 'middle' ? '50%' : anchor.y;
  return `${x} ${y}`;
}

export function ResponsiveSquareCanvas({
  anchor,
  style,
  className,
  render,
  animate,
}: ResponsiveSquareCanvasProps): ReactElement {
  const fieldAnchor: ResponsiveAnchor = anchor || defaultAnchor;
  const theme = useAtomValue(ThemeAtom);
  const redrawField = useAtomValue(FieldConfigHashAtom);
  const fieldViz = useAtomValue(FieldVizPercentAtom);
  const containerRef = useRef<HTMLDivElement>(null);
  const mainRef = useRef<HTMLCanvasElement>(null);
  const cacheRef = useRef<HTMLCanvasElement>(null); // Offscreen cache
  const requestRef = useRef<number>(null);
  const observerRef = useRef<ResizeObserver>(null);

  // Okay, so to enable animation without re-rendering the whole canvas,
  // we create a memory image to blit for every animation frame.

  // First, create the cached canvas:
  useEffect(() => {
    if (!mainRef.current || !animate) {
      return;
    }
    const main = mainRef.current;
    cacheRef.current = document.createElement('canvas');
    cacheRef.current.width = main.width;
    cacheRef.current.height = main.height;
  }, [animate]);

  useEffect(() => {
    const element = containerRef.current;
    if (!element) return;

    observerRef.current = new ResizeObserver((entries) => {
      for (const entry of entries) {
        // Use devicePixelContentBoxSize for sharp rendering on HiDPI screens
        // const size = entry.devicePixelContentBoxSize[0];
        const size = entry.contentBoxSize[0];
        if (isUndefined(size)) {
          continue;
        }
        const newSize = Math.min(size.inlineSize, size.blockSize);

        const main = mainRef.current;
        const cache = cacheRef.current;

        if (isNull(main) || (animate && isNull(cache))) {
          continue;
        }
        // 1. Resize both canvases (this clears them)
        main.width = newSize;
        main.height = newSize;
        if (animate) {
          cache!.width = newSize;
          cache!.height = newSize;
        }
        // 2. Immediately redraw the background to the new cache size
        // This ensures the cache is never empty or stretched
        const ctx = animate ? cache!.getContext('2d') : main.getContext('2d');
        if (isNull(ctx)) {
          continue;
        }
        render(ctx, window.devicePixelRatio || 1);
      }
    });

    // Observe the container using 'device-pixel-content-box' for crisp canvas rendering
    observerRef.current.observe(element, { box: 'device-pixel-content-box' });

    return () => observerRef.current?.disconnect();
  }, [
    animate,
    render,
    redrawField /* TODO: This should trigger for any changes to the paths! */,
  ]); // Re-bind if background logic changes

  // Animation Loop (Unchanged, just uses current canvas.width/height)
  const animateFrame = () => {
    if (!animate) return;
    const main = mainRef.current;
    const cache = cacheRef.current;
    if (!main || !cache) return;

    const ctx = main.getContext('2d');
    if (isNull(ctx)) {
      return;
    }
    ctx.clearRect(0, 0, main.width, main.height);

    // Draw the fresh cache (which was updated on resize)
    ctx.drawImage(cache, 0, 0);

    animate(ctx, window.devicePixelRatio || 1);
    requestRef.current = requestAnimationFrame(animateFrame);
  };

  useEffect(() => {
    if (animate) {
      requestRef.current = requestAnimationFrame(animateFrame);
      return () => cancelAnimationFrame(requestRef.current!);
    }
  }, [animate]);

  return (
    <div
      ref={containerRef}
      style={{
        flexGrow: 1,
        display: 'flex',
        width: '100%',
        height: '100%',
        justifyContent: translateX(fieldAnchor.x),
        alignItems: translateY(fieldAnchor.y),
        overflow: 'hidden',
      }}>
      <img
        src={`/assets/field-${theme}.jpg`}
        style={{
          objectPosition: getObjectPos(fieldAnchor),
          opacity: fieldViz,
          width: '100%',
          height: '100%',
          objectFit: 'contain',
        }}
      />
      <canvas
        style={{ ...style, position: 'absolute' }}
        className={className}
        ref={mainRef}
      />
    </div>
  );
}
