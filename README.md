# AdvSolarPatch - Unofficial

Unofficial maintenance fork of **[Su5eD's AdvSolarPatch](https://github.com/Su5eD/AdvSolarPatch)**
for Minecraft 1.12.2. The mod is a runtime patcher for Chocohead's
**Advanced Solar Panels 4.3.0** and additionally extends its solar helmets.
The upstream repository is inactive; this fork continues maintenance and is
developed alongside [Super-Solar-Panels-Unofficial](https://github.com/mustardwheat/Super-Solar-Panels-Unofficial).

## What it fixes

- Crashing of Advanced Solar Panels 4.3.0 with newer IC2 builds (2.8.191+),
  caused by changed `InvSlot` family constructors. The patch rewrites the call
  descriptors at runtime via Mixin:
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlotProcessable.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;ILic2/api/recipe/IMachineRecipeManager;)V`
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlotOutput.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;I)V`
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlot.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;Lic2/core/block/invslot/InvSlot$Access;ILic2/core/block/invslot/InvSlot$InvSide;)V`
- Double Stone Slab duplicating adjacent blocks: the `advanced_solar_panels:double_stone_slab`
  item and its recipe are removed entirely (upstream only patched the placement
  logic). On first load of an old world, FML asks you to confirm the missing
  registry entry; this is safe to accept.

## Features

- Configurable maximum output (EU/t) for the four ASP solar panels, via keys in
  the `solars` category of the ASP config file:
  - `AdvancedSPMaxOutput` (default 32)
  - `HybrydSPMaxOutput` (default 128)
  - `UltimateHSPMaxOutput` (default 512)
  - `QuantumSPMaxOutput` (default 8192)
- Hybrid and Ultimate Hybrid Solar Helmets implement IC2's `IHazmatLike`:
  with charge remaining, they complete a four-piece radiation protection set
  together with any quantum-family armor. The Advanced Solar Helmet is
  intentionally excluded, matching its nano-family positioning.
- Hybrid and Ultimate Hybrid Solar Helmets gain the quantum-helmet feature set:
  clearing negative potion effects for an EU cost, automatically eating canned
  food when hungry (1000 EU per use), and a night vision mode toggled with the
  mode switch key (Alt by default). In bright light the mode simply disables
  night vision instead of applying blindness.
- All three solar helmets show their day/night generation in the item tooltip,
  reading the live values from the solar panel configuration.

## Requirements

| Mod | Version | Notes |
|---|---|---|
| Minecraft | 1.12.2 | Forge 14.23.5.2847 or newer |
| IndustrialCraft 2 | 2.8.191 or newer | older IC2 builds do not need this mod |
| Advanced Solar Panels (Chocohead) | 4.3.0 | the patch target |
| MixinBooter | 10.2 or newer | present in most 1.12.2 modpacks |

## Building

The build is driven by RetroFuturaGradle on Gradle 8.12 (migrated from the
upstream ForgeGradle + Kotlin DSL setup). Use the included wrapper; no system
Gradle installation is required.

Prerequisites:

- A JDK that can run Gradle 8.12 (Java 17 or newer) available on `JAVA_HOME`.
  The compilation toolchain targets Java 8 and is provisioned automatically.

Common tasks:

| Task | Result |
|---|---|
| `gradlew build` | compiles and packages `build/libs/advsolarpatch-<version>.jar` |
| `gradlew runClient` | starts a dev client with IC2 and Advanced Solar Panels |

## Continuous integration

- `build.yml` compiles the project and uploads the jar on every push and pull request.
- `release.yml` builds and publishes a GitHub Release whenever a `v*` tag is pushed.

## License & credits

GPL-3.0, following the upstream repository.

- Original AdvSolarPatch by **[Su5eD](https://github.com/Su5eD/AdvSolarPatch)**.
- Advanced Solar Panels by Icedfire, SeNtiMeL and **Chocohead**.
