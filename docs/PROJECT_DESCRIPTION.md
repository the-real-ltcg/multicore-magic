Gives Minecraft's chunk mesh building its own **dedicated, tunable thread pool** — so chunk loading doesn't compete with world generation and other background tasks for CPU cores.

## The problem

Vanilla builds chunk render meshes on a thread pool it **shares** with world generation, structure searches, and other misc background work. The pool is sized automatically to `CPU cores − 1`, but every one of those threads is also up for grabs by worldgen — so chunk building is constantly competing with everything else for the same cores, especially noticeable when flying or exploring new terrain quickly.

## What this mod does

Multicore Magic redirects chunk mesh building to a **separate, dedicated pool** that nothing else touches, so it no longer contends with worldgen or IO. The pool is fully configurable and can be resized **live, in-game, with no restart required**.

**A note on scope:** this mod does not — and cannot — make the entire game "multicore." Minecraft's core tick loop is intentionally single-threaded and can't be safely parallelized wholesale. This mod targets one specific, real bottleneck (chunk mesh building) the same way mods like Lithium or Starlight target specific subsystems, rather than promising generic multithreading of the whole engine.

## Features

- Dedicated chunk-builder thread pool, fully decoupled from vanilla's shared background executor
- Live-resizable — change the thread count mid-game and see the effect immediately
- Mod Menu + Cloth Config screen with tooltips
- In-game status command for active/queued task counts
- Sensible default, tunable up to 2x your core count for experimentation

## Commands

- `/multicoremagic status` — shows enabled state, thread count, active/queued tasks
- `/multicoremagic threads <n>` — resizes the pool live, no restart needed
- `/multicoremagic on` — enables the dedicated pool
- `/multicoremagic off` — disables it, falling back to vanilla's shared pool

`on` / `off` take effect on the next world join or resource reload (F3+A); `threads` applies immediately.

The default thread count matches vanilla's own formula (CPU cores − 1). The slider goes up to cores x 2 for experimentation, but pushing it too high alongside vanilla's own worldgen pool can cause oversubscription (both pools competing for the same cores) under heavy load like flying through new terrain — dial it back if you notice stutter after raising it.

## Requirements

- Minecraft 26.2
- Fabric Loader 0.18.4 or newer
- Fabric API
- Java 25

## License

MIT — source on [GitHub](https://github.com/the-real-ltcg/multicore-magic).
