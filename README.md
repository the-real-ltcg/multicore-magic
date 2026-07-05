# Multicore Magic

![Minecraft](https://img.shields.io/badge/Minecraft-26.2%20%7C%2026.1.2-4E7A31?logo=minecraft&logoColor=white)
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

Two Fabric builds are published, one per supported Minecraft version:

| Jar | Minecraft | Fabric Loader | Fabric API | Java |
|---|---|---|---|---|
| `multicore-magic-fabric-26.2-*.jar` | `26.2` | `>= 0.18.4` | any 26.2 build | `25` |
| `multicore-magic-fabric-26.1-*.jar` | `26.1.2` | `>= 0.18.4` | any 26.1.2 build | `25` |

The two versions renamed the internal method this mod hooks into (`allChanged()` on 26.1.2 →
`invalidateCompiledGeometry(...)` on 26.2), so each jar ships its own small version-specific mixin
— everything else is identical between the two.

## NeoForge

Both Fabric jars also run unmodified on NeoForge via
[Launchpad](https://github.com/Sinytra/Launchpad), which loads Fabric-format mods directly from
their `fabric.mod.json` metadata (this mod opts in via `"launchpad:compatible": true` — no code
changes needed). On NeoForge you'll additionally need
[Forgified Fabric API](https://github.com/Sinytra/ForgifiedFabricAPI) in place of regular Fabric
API. The Mod Menu config screen isn't available on NeoForge (Mod Menu itself is Fabric-only) — use
the `/multicoremagic` commands there instead.

> [!IMPORTANT]
> **Launchpad currently only has builds for Minecraft 26.1.2, not 26.2.** Use the
> `fabric-26.1` jar with Launchpad + Forgified Fabric API on NeoForge `26.1.2`. The `fabric-26.2`
> jar is ready for NeoForge the moment Launchpad releases a 26.2 build, but can't be loaded on
> NeoForge today. Check [Launchpad's releases](https://github.com/Sinytra/Launchpad/releases) for
> current status.

## Building

```sh
JAVA_HOME=<path to JDK 25> ./gradlew build
```

Builds both `fabric-26.2` and `fabric-26.1` jars into their respective `build/libs/` folders. To
build just one: `./gradlew :fabric-26.2:build` or `./gradlew :fabric-26.1:build`.

## License

[MIT](LICENSE)
