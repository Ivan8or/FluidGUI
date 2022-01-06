package ivan8or.nagui;

public final class FluidGUI {

}



/*
transitions:

  __head__:
  # items to spawn to get back
  # to what __head__ looks like (from __head__)
    __head__:
      - item: "minecraft:honeycomb"
        id: "loopback"
        slot: 12
        delay: 0

      - item: "minecraft:redstone_dust"
        id: "to_settings"
        slot: 0
        delay: 0

  # items to spawn to get to
  # what settings looks like from __head__
    settings:
      - item: "minecraft:arrow"
        id: "back"
        slot: 53
        delay: 0

  settings:
  # items to spawn to get back
  # to what __head__ looks like from settings
    __head__:
      - item: "minecraft:honeycomb"
        id: "loopback"
        slot: 12
        delay: 0

      - item: "minecraft:redstone_dust"
        id: "to_settings"
        slot: 0
        delay: 0


functions:
  - send:
      player: string
      server: string
      message: string

    startworld:
      owner: string
      slot: int


 */