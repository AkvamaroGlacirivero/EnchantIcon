# 如何为附魔标签添加自定义图标？

在所有版本中，你可以使用资源包来为附魔标签模组添加自定义图标。

以下为添加自定义图标的说明。

## 步骤一：为新附魔画图标！

无需解释。

## 步骤二：确定附魔 ID

在 Minecraft 中，每个附魔都有一个唯一 ID。附魔标签模组使用这个 ID 确定附魔对应的图标。

因此，你需要知道附魔对应的 ID。

新版附魔标签可帮助你确定正确的 ID。下面是具体方法：

  1. 在配置选项中，找到 `showEnchantInternalName` 或 `show_enchant_internal_name` 选项。 
  2. 将该选项的值改为 `true`。
  3. 现在你可以在工具提示中找到每个附魔对应的 ID。

## 步骤三：创建资源包

假设你拿到的附魔 ID 是 `twilightforest:chill_aura`。 

你将需要创建这些文件：

  - `assets/enchant_icon/models/enchant/twilightforest/chill_aura.json`
  - `assets/enchant_icon/textures/enchant_icon/twilightforest/chill_aura.png`
  - `pack.mcmeta`

在树形结构下，他们看起来像这样：

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

### 模型文件 `chill_aura.json` 

每个图标都会包装成模型。模型文件的格式和原版物品模型文件一样。

将下列内容复制进 `chill_aura.json`：

```json
{
  "parent": "minecraft:item/generated", 
  "textures": {
    "layer0": "enchant_icon:enchant_icon/twilightforest/chill_aura.png"
  }
}
```

### `chill_aura.png`

还记得步骤一吗？这就是你棒极了的新贴图。

### `pack.mcmeta`

将下列内容复制进 `pack.mcmeta`

```json
{
  "pack": {
    "pack_format": 32,
    "description": "Add Twilight Forest support to EnchantIcon mod."
  }
}
```

32 意味着该资源包适用于 Minecraft 1.20.5.

更多信息可在这找到：https://minecraft.wiki/w/Tutorials/Creating_a_resource_pack#Formatting_pack.mcmeta

## 步骤四：创建 Zip 压缩包

确保 `pack.mcmeta` 位于 zip 的根目录下！

更多信息可参考：https://minecraft.wiki/w/Tutorials/Creating_a_resource_pack

## 步骤五：完成

安装该资源包即可为附魔标签模组添加全新图标了。

## 附录一：我想为附魔标签提交全新贴图！

过程基本和上述一致，但你需要放对文件位置。假设附魔 ID 是 `twilight_forest:fire_react`

- 模型文件应放在 `shared-assets/src/main/resources/assets/enchant_icon/models/enchant/twilightforest/fire_react.json`
- 贴图文件应放在 `shared-assets/src/main/resources/assets/enchant_icon/textures/enchant_icon/twilightforest/fire_react.png`

