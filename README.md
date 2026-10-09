# Functional Spyglass

Fabric mod for Minecraft **26.1**. Looking through a vanilla spyglass has **three zoom levels**. Left-click while scoping cycles 1 → 2 → 3 → 1. Lowering the glass resets to level 1.

Level 1 is a slight zoom versus unaided FOV. Level 2 is a bit less zoomed than vanilla spyglass. Level 3 is a bit more zoomed than vanilla spyglass. FOV numbers live in `config/functional_spyglass.json` and can be tweaked from the pack Debug admin once Lane B lands.

## Compatibility

| Component     | Version           |
|---------------|-------------------|
| Minecraft     | 26.1              |
| Fabric Loader | 0.19.3+           |
| Fabric API    | 0.145.1+26.1      |
| Java          | 25+               |

## Build

From this directory:

```bash
./gradlew build
```

The output JAR is at `build/libs/functional_spyglass-1.0.0.jar` (version from `gradle.properties`).

## Test

```bash
./gradlew test          # unit tests (JUnit + Fabric Loader bootstrap)
./gradlew runGameTest   # server GameTests
./gradlew build         # full CI gate (includes both)
```

Play the indexed local set from [`../dev-pack`](../dev-pack/README.md) (`./gradlew runClient` there).

## Install

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.1.
2. Download [Fabric API](https://modrinth.com/mod/fabric-api) for 26.1 and place it in your mods folder.
3. Copy the built JAR into your Minecraft `mods` folder (client is enough for this feature).

## Branch Policy

Each supported Minecraft version has its own long-lived branch named after the exact `minecraft_version` in `gradle.properties`. Version branches are the source of truth; `main` is not used.

- **Default branch:** `26.1`
- **Feature/fix work:** branch from the target version as `{version}/feature-name`, merge back into the version branch

```bash
git clone https://github.com/spicato-minecraft/functional_spyglass.git
cd functional_spyglass
git checkout 26.1
```

GitHub remote is created when preparing a pack/player ship. Local work uses the version branch from day one.

## Releases and Modrinth

Merging code does **not** publish to Modrinth. Publishing happens only when a GitHub Release is created.

Release tags use `{minecraft_version}-{mod_version}` (e.g. `26.1-1.0.0`), matching `gradle.properties`.

1. Merge your changes into the version branch (e.g. `26.1`).
2. Bump `mod_version` in `gradle.properties` if shipping new player-facing changes.
3. Add a changelog at `releases/{tag}.md` — see `releases/TEMPLATE.md`.
4. Create a GitHub Release with tag `{minecraft_version}-{mod_version}` targeting the version branch.

See [minecraft-mods/docs/README.md](../docs/README.md) for org-wide release conventions.

## Test Strategy

| Layer      | Purpose |
|------------|---------|
| Unit tests | Zoom wrap/reset; config reload from a temp dir |
| GameTests  | Smoke load; later: scoping + attack does not damage |

Behavior tests are tracked in [Notion Tasks](https://app.notion.com/p/3b716e8d4da380adbd93cf0a73b39d5c). Manual UAT items live in the Mod Catalog.

## Architecture

Client mixins replace vanilla spyglass FOV scale (0.1 of option FOV) and consume attack while scoping to cycle zoom (vanilla discards those clicks). Scoped look is vanilla spyglass yaw times `look_speed × mix(look_min, look_max, zoomT ^ look_curve)`. `look_min` is level 1 (low zoom); `look_max` is level 3 (high zoom). Curve `0` stays at min for every level; raising it speeds high magnification toward max. Tune from pack Debug admin (Spyglass). Config uses nested keys under `zoom` so `spicato_admin` can bind sliders without a compile dependency. Gameplay never imports the admin JAR.

Proposal: [Functional spyglass — three zoom levels](https://app.notion.com/p/3d016e8d4da3810896eecab30b3134ad).
