# Legends of Trailstone

2D adventure game — Kotlin + Canvas rendering, built for Android (minSdk 26).

## Structure
```
app/src/main/java/com/trailstone/adventure/
  MainActivity.kt            -> menu screen (logo + play button)
  ChapterSelectActivity.kt    -> 5-chapter select screen, lock icons, saved progress
  GameActivity.kt              -> hosts GameView for the chosen chapter
  GameView.kt                  -> game loop, physics, collisions, HUD, touch controls
  Player.kt                    -> player state machine (idle/walk/jump/attack/hurt/victory)
  Enemies.kt                   -> goblin / bat / golem behaviour
  Items.kt                     -> coin / key / heart / potion
  LevelData.kt                 -> per-chapter enemy & item placement

app/src/main/res/drawable-nodpi/   -> all game art (see "Assets" below)
.github/workflows/build.yml         -> GitHub Actions CI, builds app-debug.apk
```

## How each chapter works
Single fixed-camera arena per chapter (not a scrolling level). Defeat every enemy
and collect the key to clear the chapter — this unlocks the next one and saves
automatically (SharedPreferences, survives app restart).

- **Coin** — adds to score counter, no other effect
- **Heart** — restores 1 heart (max 3)
- **Potion** — currently just starts a 5-second timer (`player.powerTimer`) with
  no effect wired up yet — hook up whatever bonus you want (speed, damage, etc.)
  in `GameView.update()`
- **Key** — required, along with defeating all enemies, to clear the chapter

Controls are drawn directly on the canvas (semi-transparent circles, bottom
corners) since no separate button art was made for movement/attack — only
menu buttons (play/pause/restart/settings) use your uploaded art.

## Build via GitHub Actions (matches your usual workflow)
```
cd trailstone
git init
git add .
git commit -m "Initial Legends of Trailstone build"
git branch -M main
git remote add origin https://github.com/elephantcos-cloud/legends-of-trailstone.git
git push -u origin main
```
Actions will run automatically on push to `main`, generate the Gradle wrapper,
build `app-debug.apk`, and attach it under the workflow run's **Artifacts**.

## Known placeholders — swap these out when ready
These 6 files were not available yet, so simple placeholders were generated so
the project builds and runs right now. Replace the file and rebuild — no code
changes needed since everything is referenced by name:

- `item_coin.png` (drawn gold star-coin)
- `bg_jungle.png`, `bg_desert.png`, `bg_cave.png`, `bg_snow.png`, `bg_temple.png`
  (flat gradients)
- `mipmap-*/ic_launcher.png` (simple compass icon) — replace with a proper app icon anytime

## Things to expect on first build
Gradle/AGP version mismatches or missing-resource errors are normal on a first
CI run for a hand-written project like this — same as your last few apps. Send
me the Actions log and we'll fix it together.
