# Advanced Solar Panels Patcher
Fixes several issues in Advanced Solar Panels.

Fixed errors:
- Crashing with newer IC2 builds (2.8.191+)
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlotProcessable.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;ILic2/api/recipe/IMachineRecipeManager;)V`
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlotOutput.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;I)V`
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlot.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;Lic2/core/block/invslot/InvSlot$Access;ILic2/core/block/invslot/InvSlot$InvSide;)V`
- Double Stone Slab duplicating adjacent blocks  

Features:
- Configurable max output (Max Output, EU/t) for the four solar panels, via new keys in the solars category of the ASP config file:
  - AdvancedSPMaxOutput (default 32)
  - HybrydSPMaxOutput (default 128)
  - UltimateHSPMaxOutput (default 512)
  - QuantumSPMaxOutput (default 8192)
