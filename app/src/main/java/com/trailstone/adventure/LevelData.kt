package com.trailstone.adventure

data class EnemySpawn(val type: String, val xFraction: Float)
data class ItemSpawn(val type: String, val xFraction: Float)

data class ChapterData(
    val id: Int,
    val name: String,
    val background: Int,
    val enemies: List<EnemySpawn>,
    val items: List<ItemSpawn>
)

object Levels {

    fun getChapter(id: Int): ChapterData = chapters.first { it.id == id }

    val chapters = listOf(
        ChapterData(
            id = 1,
            name = "Jungle",
            background = R.drawable.bg_jungle,
            enemies = listOf(
                EnemySpawn("goblin", 0.55f),
                EnemySpawn("goblin", 0.80f)
            ),
            items = listOf(
                ItemSpawn("coin", 0.30f),
                ItemSpawn("coin", 0.45f),
                ItemSpawn("heart", 0.65f),
                ItemSpawn("key", 0.90f)
            )
        ),
        ChapterData(
            id = 2,
            name = "Desert",
            background = R.drawable.bg_desert,
            enemies = listOf(
                EnemySpawn("goblin", 0.45f),
                EnemySpawn("bat", 0.75f)
            ),
            items = listOf(
                ItemSpawn("coin", 0.25f),
                ItemSpawn("coin", 0.60f),
                ItemSpawn("potion", 0.40f),
                ItemSpawn("key", 0.88f)
            )
        ),
        ChapterData(
            id = 3,
            name = "Cave",
            background = R.drawable.bg_cave,
            enemies = listOf(
                EnemySpawn("bat", 0.40f),
                EnemySpawn("bat", 0.70f)
            ),
            items = listOf(
                ItemSpawn("coin", 0.30f),
                ItemSpawn("heart", 0.50f),
                ItemSpawn("potion", 0.65f),
                ItemSpawn("key", 0.90f)
            )
        ),
        ChapterData(
            id = 4,
            name = "Snow Mountain",
            background = R.drawable.bg_snow,
            enemies = listOf(
                EnemySpawn("goblin", 0.35f),
                EnemySpawn("bat", 0.55f),
                EnemySpawn("goblin", 0.80f)
            ),
            items = listOf(
                ItemSpawn("coin", 0.20f),
                ItemSpawn("coin", 0.45f),
                ItemSpawn("heart", 0.65f),
                ItemSpawn("key", 0.90f)
            )
        ),
        ChapterData(
            id = 5,
            name = "Ancient Temple",
            background = R.drawable.bg_temple,
            enemies = listOf(
                EnemySpawn("golem", 0.70f)
            ),
            items = listOf(
                ItemSpawn("coin", 0.20f),
                ItemSpawn("coin", 0.35f),
                ItemSpawn("potion", 0.50f),
                ItemSpawn("key", 0.85f)
            )
        )
    )
}
