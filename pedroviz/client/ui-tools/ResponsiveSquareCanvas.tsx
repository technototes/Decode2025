import {
  CSSProperties,
  ReactElement,
  useEffect,
  useRef,
  useState,
} from 'react';

export type ResponsiveAnchor = {
  x: 'left' | 'center' | 'right';
  y: 'top' | 'middle' | 'bottom';
};

const defaultAnchor: ResponsiveAnchor = { x: 'center', y: 'middle' };

export type ResponsiveSquareCanvasProps = {
  anchor?: ResponsiveAnchor;
  style?: CSSProperties;
  className?: string;
  render: (
    ctx: CanvasRenderingContext2D,
    size: number,
    devicePixelRatio: number,
  ) => void;
};
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

export function ResponsiveSquareCanvas({
  anchor,
  style,
  className,
  render,
}: ResponsiveSquareCanvasProps): ReactElement {
  const fieldAnchor: ResponsiveAnchor = anchor || defaultAnchor;
  const containerRef = useRef<HTMLDivElement>(null);
  const canvasRef = useRef<HTMLCanvasElement>(null);
  const [size, setSize] = useState(0);

  // Make the canvas resize
  useEffect(() => {
    if (!containerRef.current) return;
    const observer = new ResizeObserver(([entry]) => {
      if (entry) {
        const { width, height } = entry.contentRect;
        // Floor the value to prevent fractional pixel jittering during fast drags
        setSize(Math.floor(Math.min(width, height)));
      }
    });
    observer.observe(containerRef.current);
    return () => observer.disconnect();
  }, [containerRef]);

  // Rendering has a side effect, so it goes in a useEffect
  useEffect(() => {
    const canvas = canvasRef.current;
    if (!canvas || size === 0) return;
    const ctx = canvas.getContext('2d');
    if (!ctx) return;

    const dpr = window.devicePixelRatio || 1;

    canvas.width = size * dpr;
    canvas.height = size * dpr;
    canvas.style.width = `${size}px`;
    canvas.style.height = `${size}px`;

    render(ctx, size, dpr);
  }, [canvasRef, size]);

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
      }}>
      {size > 0 && (
        <canvas style={style} className={className ?? ''} ref={canvasRef} />
      )}
    </div>
  );
}
