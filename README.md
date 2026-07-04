# Multicore Magic

![Minecraft](https://img.shields.io/badge/Minecraft-26.2-4E7A31?logo=minecraft&logoColor=white)
![Fabric](https://img.shields.io/badge/Loader-Fabric-1976D2?logo=fabric&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

Gives Minecraft's chunk mesh building its own **dedicated, tunable thread pool** — so chunk
loading doesn't compete with world generation and other background tasks for CPU cores.

## The problem

Vanilla builds chunk render meshes on `Util.backgroundExecutor()`, a thread pool it **shares**
with world generation, structure searches, and other misc background work. The pool is sized
automatically to `CPU cores − 1`, but every one of those threads is also up for grabs by
worldgen — so chunk building is constantly competing with everything else for the same cores,
especially noticeable when flying or exploring new terrain quickly.

## What this mod does

Multicore Magic redirects chunk mesh building to a **separate, dedicated pool** that nothing else
touches, so it no longer contends with worldgen or IO. The pool is fully configurable and can be
resized **live, in-game, with no restart required**.

> [!NOTE]
> This mod does not — and cannot — make the entire game "multicore." Minecraft's core tick loop is
> intentionally single-threaded and can't be safely parallelized wholesale. This mod targets one
> specific, real bottleneck (chunk mesh building) the same way mods like Lithium or Starlight
> target specific subsystems, rather than promising generic multithreading of the whole engine.

## Features

- 🧵 Dedicated chunk-builder thread pool, fully decoupled from vanilla's shared background executor
- ⚡ Live-resizable — change the thread count mid-game and see the effect immediately
- 🎛️ Mod Menu + Cloth Config screen with tooltips
- 📊 In-game status command for active/queued task counts
- 🔧 Sensible default, tunable up to 2× your core count for experimentation

## Usage

| Command | Effect |
|---|---|
| `/multicoremagic status` | Shows enabled state, thread count, active/queued tasks |
| `/multicoremagic threads <n>` | Resizes the pool live — no restart needed |
| `/multicoremagic on` | Enables the dedicated pool |
| `/multicoremagic off` | Disables it, falling back to vanilla's shared pool |

`on`/`off` take effect on the next world join or resource reload (<kbd>F3</kbd>+<kbd>A</kbd>);
`threads` applies immediately.

The default thread count matches vanilla's own formula (CPU cores − 1). The slider goes up to
cores × 2 for experimentation, but pushing it too high alongside vanilla's own worldgen pool can
cause **oversubscription** (both pools competing for the same cores) under heavy load like flying
through new terrain — dial it back if you notice stutter after raising it.

## Requirements

- Minecraft `26.2`
- Fabric Loader `>= 0.18.4`
- Fabric API
- Java `25`

## Building

```sh
JAVA_HOME=<path to JDK 25> ./gradlew build
```

## License

[MIT](LICENSE)
