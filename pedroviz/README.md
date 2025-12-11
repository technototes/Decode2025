# A PedroPath visualizer

The **goal** is for this to be a _local_ application that let's you vizualize
and edit PedroPath's. The _reasons_ for this as opposed to just using
[vizualizer.pedropathing.com](https://visualizer.pedropathing.com/) are twofold:

1. When you're connected to the bot (for deployment, debugging, or using a
   panel) you can't use the Visualizer, so you have to launch it, then switch
   your wifi. BOOO!
2. This should integrate into your code. No more copying stuff back and forth!
   It will create the class for you, and allow you to name points, instead of
   just having random numerical names.

## Stuff I jotted down earlier:

- Image under a canvas that allows you to drag & drop points for paths.
- Names are auto generated, but editable.
- Select a robot path code source (different bots/teams)

**Tasks, in order:**

- [x] Read simple path from code
- [x] Display that path on the canvas.
- [ ] Allow creation & editing values in a text field
  - [ ] Edit an existing named value, pose, curve, chain
- [ ] Reflect those changes in the code
  - [ ] Checksum the code to detect external edits?
- [ ] Allow editing points by dragging & dropping on the canvas
- [ ] Highlight hovered-over paths/curves/points (both directions)
- [ ] Animate the robot along the path
- [ ] Put the field graphic under the canvas
- [ ] Have a grid key near/under the canvas
- [ ] Enable "warning" lines: warn if the robot crosses a line on a path
- [ ] Specify different alliance paths
  - [ ] Bonus: Reflect a path along a line or axis (with or without a bot
        offset?)
- [ ] Support additional parts of the path builder
  - [ ] setMaxPowerScaling, etc...
- [ ] Maintain any code that I don't actually parse (keep chunks of code that
      aren't represented in the UI)
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
