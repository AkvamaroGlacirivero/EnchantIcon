# How to add your own icons to EnchantIcons mod

All versions of EnchantIcons mod support adding new icons via resource packs.

Here is the walkthrough guide of how to do it.

## Step 1: Draw the texture for new enchantments!

No need to explain this. 

## Step 2: Get the enchantment ID

In Minecraft, each enchantment has a unique ID. EnchantIcon use this ID to find the icon. 
This ID is also called "registry names" by modders.

You need to know the enchantment IDs of corresponding enchantments.

Newer versions of EnchantIcons mod can help you to determine the correct IDs. Here is the how-to:

  1. In config, find the option `showEnchantInternalName` or `show_enchant_internal_name`. 
  2. Change its value to `true`.
  3. Then you can find the correct IDs in the tooltips.

## Step 3: Create the resource pack

Suppose that you have enchantment ID `twilightforest:chill_aura`. 

You will need these files:

  - `assets/enchant_icon/models/enchant/twilightforest/chill_aura.json`
  - `assets/enchant_icon/textures/enchant_icon/twilightforest/chill_aura.png`
  - `pack.mcmeta`

In a tree-structure, they look like

```
My Awesome New Icons/
├── assets/
│   └── enchant_icon/
│       ├── models/
│       │   └── enchant/
│       │       └── twilightforest/
│       │           └── chill_aura.json
│       └── textures/
│           └── enchant_icon/
│               └── twilightforest/
│                   └── chill_aura.png
└── pack.mcmeta
```

### `chill_aura.json` - the model

Each icon is wrapped in a model. Its format is the same as of vanilla item model.

Copy the following content into `chill_aura.json`.

```json
{
  "parent": "minecraft:item/generated", 
  "textures": {
    "layer0": "enchant_icon:enchant_icon/twilightforest/chill_aura.png"
  }
}
```

### `chill_aura.png`

Remember step 1? It is your awesome texture. Nothing special.

### `pack.mcmeta`

Copy the following content into `pack.mcmeta`

```json
{
  "pack": {
    "pack_format": 32,
    "description": "Add Twilight Forest support to EnchantIcon mod."
  }
}
```

32 means that this pack is for Minecraft 1.20.5.

More information can be found at here: 
https://minecraft.wiki/w/Tutorials/Creating_a_resource_pack#Formatting_pack.mcmeta

## Step 4: create a zip archive

Make sure `pack.mcmeta` is in the root directory of the zip!

Refer to Minecraft Wiki article for an in-depth explanation:
https://minecraft.wiki/w/Tutorials/Creating_a_resource_pack

## Step 5: Done

You are all set now.

## Appendix I: I want to submit new icons for EnchantIcon mod!

The steps are basically the same as above, but you need to figure out the right file location.

Suppose your enchantment ID is `twilight_forest:fire_react`:

- Model file should be at `shared-assets/src/main/resources/assets/enchant_icon/models/enchant/twilightforest/fire_react.json`
- Texture should be at `shared-assets/src/main/resources/assets/enchant_icon/textures/enchant_icon/twilightforest/fire_react.png`