# Multicore Magic

A Fabric client mod for Minecraft 26.2 that gives chunk mesh building its own dedicated,
live-resizable thread pool instead of sharing vanilla's background pool with worldgen, structure
searches, and other misc work.

Vanilla builds chunk render meshes on `Util.backgroundExecutor()`, a pool shared with everything
else running in the background (sized `cores - 1`). This mod redirects the renderer to a separate
pool used only for chunk building, so it doesn't contend with that other work, and exposes the
thread count as a live, tunable setting instead of a fixed JVM-computed value.

## Usage

- `/multicoremagic status` — enabled state, thread count, active/queued tasks
- `/multicoremagic threads <n>` — resize the pool live, no restart needed
- `/multicoremagic on` / `/multicoremagic off` — toggle (takes effect on next world join or F3+A)
- Mod Menu + Cloth Config screen with the same controls

Default thread count matches vanilla's own formula (CPU cores − 1). The slider goes up to
cores × 2 for experimentation, but pushing it too high alongside vanilla's own worldgen pool can
cause oversubscription (both pools competing for the same cores) under heavy load like flying
through new terrain.

## Requirements

- Minecraft 26.2
- Fabric Loader >= 0.18.4
- Fabric API
- Java 25

## Building

```
JAVA_HOME=<path to JDK 25> ./gradlew build
```

## License

MIT
