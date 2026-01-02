# A PedroPath visualizer

The **goal** is for this to be a _local_ application that let's you vizualize
and edit PedroPath's. The _reasons_ for this as opposed to just using
[vizualizer.pedropathing.com](https://visualizer.pedropathing.com/) are twofold:

1. When you're connected to the bot (for deployment, debugging, or using a
   panel) you can't use the Visualizer, so you have to launch it, then switch
   your wifi. BOOO!
2. This should integrate into your code. No more copying stuff back and forth!
   It will create the class for you, and allow you to name points, instead of
   just having random numerical names. Honestly, using Panels to update things
   live would be _amazing_: Your code and the bot on the field are kept in sync!

**Tasks, in order:**

- [x] Read paths from code
- [x] Display those paths on the canvas.
- [ ] Allow creation:
  - [x] Named values
  - [ ] Named poses
  - [ ] Named curves
  - [ ] Named PathChains
- [ ] Edit existing:
  - [x] Named values
  - [ ] Named poses
  - [ ] Named curves
  - [ ] Named PathChains
- [ ] Reflect those changes in the code
  - [ ] Checksum the code to detect external edits?
  - [ ] When external edits have occurred, try to resolve the conflicts (ugh...)
- [ ] Allow editing points by dragging & dropping on the canvas
- [ ] Highlight hovered-over paths/curves/points (both directions)
- [ ] Animate the robot along the path
- [ ] Put the field graphic under the canvas
- [ ] Have a grid key near/under the canvas
- [ ] Enable "warning" lines: warn if the robot crosses a line on a path
  - [ ] Specify robot dimensions
- [ ] Specify different alliance paths
  - [ ] Bonus: Reflect a path along a line or axis
- [ ] Support additional parts of the path builder
  - [ ] multiple paths
  - [ ] path headings (global and last)
  - [ ] max velocity
  - [ ] braking strength
  - [ ] tValues
- [ ] Maintain any code that I don't actually parse from the source code (keep
      chunks of code that aren't represented in the UI)
  - [ ] Maintain comments

# Docs-n-stuff

To install dependencies:

```bash
bun install
```

To start a development server:

```bash
bun pvdev
```

To run for production:

```bash
bun pvstart
```

## Development

I'm using [React](https://react.dev/),
[Typescript](https://www.typescriptlang.org/), with [Jotai](https://jotai.org/)
for state management and
[FluentUI](https://developer.microsoft.com/en-us/fluentui#/) as the UI/control
toolbox. None of them are too complicated, but each have their own sets of
weirdness. Feel free to reach out to me if you're trying to understand the code,
add a feature, or fix a bug.

On the backend, everything is just written in Typescript. It made deployment
much easier. It's built and served from a `Bun.serve` invocation. I'll probably
want to figure out how to package it up in a single bundle in the future, but
for now, that's good enough.

The back end code is all served through `index.tsx` which serves up the .ts/.tsx
files from the `pedroviz` subdirectory, and runs the stuff in the `server`
subdirectory on the backend.

> > > This is probably a bad way to nest code, if I'm thinking about it

TODO: Write moar dox
