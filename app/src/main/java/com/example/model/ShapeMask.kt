package com.example.model

enum class ShapeType(val displayName: String) {
    ANCHOR("Anchor"),
    DOG("Puppy"),
    CROWN("Crown"),
    BUTTERFLY("Butterfly"),
    HEART("Heart"),
    FISH("Fish"),
    HOURGLASS("Hourglass"),
    SWORD("Sword"),
    DIAMOND("Diamond"),
    STAR("Star"),
    CAT("Kitten"),
    SHIP("Vessel"),
    TREE("Bonsai"),
    BIRD("Falcon"),
    ELEPHANT("Elephant"),
    HORSE("Knight"),
    GUITAR("Guitar"),
    CASTLE("Castle"),
    TEAPOT("Teapot"),
    OWL("Owl"),
    MOON("Crescent"),
    DRAGON("Dragon"),
    CAR("Speedster"),
    LOTUS("Lotus"),
    LION("Lion"),
    KEY("Skeleton Key"),
    COMPASS("Compass"),
    ROCKET("Apollo"),
    TURTLE("Tortoise"),
    PHOENIX("Phoenix"),
    BUNNY("Bunny"),
    COFFEE("Mug"),
    PLANE("Jet"),
    RING("Diamond Ring"),
    TROPHY("Trophy"),
    PALM("Palm Island")
}

data class ShapeMask(
    val type: ShapeType,
    val rows: Int,
    val cols: Int,
    val activeCells: Set<GridPoint>
)

object ShapeLibrary {

    fun getShapeForLevel(levelId: Int): ShapeMask {
        // Screenshot exact mappings
        if (levelId == 27) return createAnchor()
        if (levelId == 40) return createDog()

        val types = ShapeType.values()
        val index = (levelId - 1) % types.size
        val baseType = types[index]
        return createShape(baseType, levelId)
    }

    private fun createShape(type: ShapeType, levelId: Int): ShapeMask {
        return when (type) {
            ShapeType.ANCHOR -> createAnchor()
            ShapeType.DOG -> createDog()
            ShapeType.CROWN -> createCrown()
            ShapeType.BUTTERFLY -> createButterfly()
            ShapeType.HEART -> createHeart()
            ShapeType.FISH -> createFish()
            ShapeType.HOURGLASS -> createHourglass()
            ShapeType.SWORD -> createSword()
            ShapeType.DIAMOND -> createDiamond()
            ShapeType.STAR -> createStar()
            ShapeType.CAT -> createCat()
            ShapeType.SHIP -> createShip()
            ShapeType.TREE -> createTree()
            ShapeType.BIRD -> createBird()
            ShapeType.ELEPHANT -> createElephant()
            ShapeType.HORSE -> createHorse()
            ShapeType.GUITAR -> createGuitar()
            ShapeType.CASTLE -> createCastle()
            ShapeType.TEAPOT -> createTeapot()
            ShapeType.OWL -> createOwl()
            ShapeType.MOON -> createMoon()
            ShapeType.DRAGON -> createDragon()
            ShapeType.CAR -> createCar()
            ShapeType.LOTUS -> createLotus()
            ShapeType.LION -> createLion()
            ShapeType.KEY -> createKey()
            ShapeType.COMPASS -> createCompass()
            ShapeType.ROCKET -> createRocket()
            ShapeType.TURTLE -> createTurtle()
            ShapeType.PHOENIX -> createPhoenix()
            ShapeType.BUNNY -> createBunny()
            ShapeType.COFFEE -> createCoffee()
            ShapeType.PLANE -> createPlane()
            ShapeType.RING -> createRing()
            ShapeType.TROPHY -> createTrophy()
            ShapeType.PALM -> createPalm()
        }
    }

    // 1. Exact Anchor Silhouette (Level 27 screenshot)
    fun createAnchor(): ShapeMask {
        val rows = 16
        val cols = 15
        val cells = mutableSetOf<GridPoint>()

        // Top ring loop
        for (r in 0..3) {
            for (c in 5..9) {
                if (r == 0 || r == 3 || c == 5 || c == 9) cells.add(GridPoint(r, c))
            }
        }
        // Stock / Crossbar
        for (c in 2..12) {
            cells.add(GridPoint(4, c))
            cells.add(GridPoint(5, c))
        }

        // Center Shank (stem)
        for (r in 4..13) {
            cells.add(GridPoint(r, 6))
            cells.add(GridPoint(r, 7))
            cells.add(GridPoint(r, 8))
        }

        // Bottom curved arms (Flukes)
        for (c in 4..10) {
            cells.add(GridPoint(14, c))
            cells.add(GridPoint(13, c))
        }
        // Left arm curving up
        for (r in 7..13) {
            cells.add(GridPoint(r, 2))
            cells.add(GridPoint(r, 3))
        }
        cells.add(GridPoint(6, 2))
        cells.add(GridPoint(6, 3))

        // Right arm curving up
        for (r in 7..13) {
            cells.add(GridPoint(r, 11))
            cells.add(GridPoint(r, 12))
        }
        cells.add(GridPoint(6, 11))
        cells.add(GridPoint(6, 12))

        return ShapeMask(ShapeType.ANCHOR, rows, cols, cells)
    }

    // 2. Exact Puppy Dog Silhouette (Level 40 screenshot)
    fun createDog(): ShapeMask {
        val rows = 16
        val cols = 16
        val cells = mutableSetOf<GridPoint>()

        // Head and Snout (top right)
        for (r in 2..7) {
            for (c in 10..14) cells.add(GridPoint(r, c))
        }
        // Nose tip
        for (r in 3..5) cells.add(GridPoint(r, 15))
        // Ears (top)
        for (r in 0..2) {
            cells.add(GridPoint(r, 10))
            cells.add(GridPoint(r, 11))
        }

        // Neck
        for (r in 5..8) {
            for (c in 9..11) cells.add(GridPoint(r, c))
        }

        // Torso / Body
        for (r in 7..11) {
            for (c in 3..11) cells.add(GridPoint(r, c))
        }

        // Tail (left sticking up)
        for (r in 4..7) {
            cells.add(GridPoint(r, 1))
            cells.add(GridPoint(r, 2))
        }

        // Front leg
        for (r in 11..15) {
            for (c in 9..11) cells.add(GridPoint(r, c))
        }

        // Back leg
        for (r in 11..15) {
            for (c in 3..5) cells.add(GridPoint(r, c))
        }

        return ShapeMask(ShapeType.DOG, rows, cols, cells)
    }

    // 3. Royal Crown
    fun createCrown(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 9..13) for (c in 1..13) cells.add(GridPoint(r, c))
        for (r in 4..8) for (c in 1..3) cells.add(GridPoint(r, c))
        cells.add(GridPoint(3, 2))
        for (r in 2..8) for (c in 6..8) cells.add(GridPoint(r, c))
        cells.add(GridPoint(1, 7))
        for (r in 4..8) for (c in 11..13) cells.add(GridPoint(r, c))
        cells.add(GridPoint(3, 12))
        return ShapeMask(ShapeType.CROWN, rows, cols, cells)
    }

    // 4. Butterfly
    fun createButterfly(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 2..13) cells.add(GridPoint(r, 7))
        cells.add(GridPoint(0, 5))
        cells.add(GridPoint(1, 6))
        cells.add(GridPoint(0, 9))
        cells.add(GridPoint(1, 8))
        for (r in 2..7) {
            for (c in 1..6) cells.add(GridPoint(r, c))
            for (c in 8..13) cells.add(GridPoint(r, c))
        }
        for (r in 8..12) {
            for (c in 3..6) cells.add(GridPoint(r, c))
            for (c in 8..11) cells.add(GridPoint(r, c))
        }
        return ShapeMask(ShapeType.BUTTERFLY, rows, cols, cells)
    }

    // 5. Heart
    fun createHeart(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val x = (c - 7.0) / 6.0
                val y = -(r - 6.0) / 6.0
                val heartEq = (x * x + y * y - 1.0)
                if (heartEq * heartEq * heartEq - x * x * y * y * y <= 0.08) {
                    cells.add(GridPoint(r, c))
                }
            }
        }
        return ShapeMask(ShapeType.HEART, rows, cols, cells)
    }

    // 6. Fish
    fun createFish(): ShapeMask {
        val rows = 15
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        for (r in 3..11) {
            for (c in 2..11) {
                if ((r - 7.0) * (r - 7.0) / 14.0 + (c - 6.5) * (c - 6.5) / 22.0 <= 1.0) {
                    cells.add(GridPoint(r, c))
                }
            }
        }
        for (r in 2..12) {
            val dist = kotlin.math.abs(r - 7.0)
            if (dist >= 1.5) {
                cells.add(GridPoint(r, 13))
                cells.add(GridPoint(r, 14))
            }
        }
        cells.add(GridPoint(6, 12))
        cells.add(GridPoint(7, 12))
        cells.add(GridPoint(8, 12))
        return ShapeMask(ShapeType.FISH, rows, cols, cells)
    }

    // 7. Hourglass
    fun createHourglass(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 0 until rows) {
            val width = if (r <= 7) (7 - r) else (r - 7)
            for (c in (7 - width)..(7 + width)) {
                cells.add(GridPoint(r, c))
            }
        }
        for (c in 2..12) {
            cells.add(GridPoint(0, c))
            cells.add(GridPoint(14, c))
        }
        return ShapeMask(ShapeType.HOURGLASS, rows, cols, cells)
    }

    // 8. Knight Sword
    fun createSword(): ShapeMask {
        val rows = 16
        val cols = 13
        val cells = mutableSetOf<GridPoint>()
        for (r in 0..10) {
            cells.add(GridPoint(r, 5))
            cells.add(GridPoint(r, 6))
            cells.add(GridPoint(r, 7))
        }
        cells.add(GridPoint(0, 6))
        for (c in 1..11) cells.add(GridPoint(11, c))
        for (r in 12..14) cells.add(GridPoint(r, 6))
        for (c in 5..7) cells.add(GridPoint(15, c))
        return ShapeMask(ShapeType.SWORD, rows, cols, cells)
    }

    // 9. Diamond
    fun createDiamond(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (kotlin.math.abs(r - 7) + kotlin.math.abs(c - 7) <= 6) {
                    cells.add(GridPoint(r, c))
                }
            }
        }
        return ShapeMask(ShapeType.DIAMOND, rows, cols, cells)
    }

    // 10. Star
    fun createStar(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 2..12) for (c in 2..12) {
            if (kotlin.math.abs(r - 7) <= 2 || kotlin.math.abs(c - 7) <= 2) {
                cells.add(GridPoint(r, c))
            }
            if (kotlin.math.abs(r - c) <= 2 || kotlin.math.abs(r - (14 - c)) <= 2) {
                cells.add(GridPoint(r, c))
            }
        }
        return ShapeMask(ShapeType.STAR, rows, cols, cells)
    }

    // 11. Cat
    fun createCat(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        cells.add(GridPoint(1, 4))
        cells.add(GridPoint(2, 4))
        cells.add(GridPoint(1, 10))
        cells.add(GridPoint(2, 10))
        for (r in 2..6) for (c in 4..10) cells.add(GridPoint(r, c))
        for (r in 7..12) for (c in 3..11) cells.add(GridPoint(r, c))
        for (c in 4..6) cells.add(GridPoint(13, c))
        for (c in 8..10) cells.add(GridPoint(13, c))
        for (r in 8..11) cells.add(GridPoint(r, 13))
        cells.add(GridPoint(7, 12))
        return ShapeMask(ShapeType.CAT, rows, cols, cells)
    }

    // 12. Sailing Ship
    fun createShip(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (c in 2..12) cells.add(GridPoint(12, c))
        for (c in 3..11) cells.add(GridPoint(13, c))
        for (r in 2..11) cells.add(GridPoint(r, 7))
        for (r in 4..10) for (c in 8..12) cells.add(GridPoint(r, c))
        for (r in 5..10) for (c in 3..6) cells.add(GridPoint(r, c))
        return ShapeMask(ShapeType.SHIP, rows, cols, cells)
    }

    // 13. Tree
    fun createTree(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 1..8) {
            for (c in 2..12) {
                if ((r - 5.0) * (r - 5.0) / 10.0 + (c - 7.0) * (c - 7.0) / 22.0 <= 1.0) {
                    cells.add(GridPoint(r, c))
                }
            }
        }
        for (r in 8..13) {
            cells.add(GridPoint(r, 6))
            cells.add(GridPoint(r, 7))
            cells.add(GridPoint(r, 8))
        }
        for (c in 4..10) cells.add(GridPoint(14, c))
        return ShapeMask(ShapeType.TREE, rows, cols, cells)
    }

    // 14. Falcon / Bird
    fun createBird(): ShapeMask {
        val rows = 15
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        for (c in 1..14) cells.add(GridPoint(5, c))
        for (c in 2..13) cells.add(GridPoint(6, c))
        for (c in 4..11) cells.add(GridPoint(7, c))
        for (r in 3..11) {
            cells.add(GridPoint(r, 7))
            cells.add(GridPoint(r, 8))
        }
        cells.add(GridPoint(2, 8))
        cells.add(GridPoint(12, 7))
        cells.add(GridPoint(12, 8))
        cells.add(GridPoint(13, 6))
        cells.add(GridPoint(13, 9))
        return ShapeMask(ShapeType.BIRD, rows, cols, cells)
    }

    // 15. Elephant
    fun createElephant(): ShapeMask {
        val rows = 16
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        // Body
        for (r in 5..11) for (c in 4..13) cells.add(GridPoint(r, c))
        // Head
        for (r in 3..7) for (c in 1..5) cells.add(GridPoint(r, c))
        // Trunk curving down and up
        for (r in 7..12) cells.add(GridPoint(r, 1))
        cells.add(GridPoint(12, 2))
        cells.add(GridPoint(11, 2))
        // Legs
        for (r in 11..15) {
            cells.add(GridPoint(r, 4))
            cells.add(GridPoint(r, 5))
            cells.add(GridPoint(r, 8))
            cells.add(GridPoint(r, 9))
            cells.add(GridPoint(r, 12))
            cells.add(GridPoint(r, 13))
        }
        return ShapeMask(ShapeType.ELEPHANT, rows, cols, cells)
    }

    // 16. Knight Horse
    fun createHorse(): ShapeMask {
        val rows = 16
        val cols = 14
        val cells = mutableSetOf<GridPoint>()
        // Ears
        cells.add(GridPoint(1, 4))
        // Head & Muzzle
        for (r in 2..6) for (c in 2..7) cells.add(GridPoint(r, c))
        for (r in 4..6) cells.add(GridPoint(r, 1))
        // Neck & Mane
        for (r in 6..10) for (c in 4..8) cells.add(GridPoint(r, c))
        // Body
        for (r in 9..12) for (c in 3..11) cells.add(GridPoint(r, c))
        // Legs
        for (r in 12..15) {
            cells.add(GridPoint(r, 4))
            cells.add(GridPoint(r, 5))
            cells.add(GridPoint(r, 9))
            cells.add(GridPoint(r, 10))
        }
        return ShapeMask(ShapeType.HORSE, rows, cols, cells)
    }

    // 17. Guitar
    fun createGuitar(): ShapeMask {
        val rows = 16
        val cols = 12
        val cells = mutableSetOf<GridPoint>()
        // Headstock
        for (r in 0..2) for (c in 5..6) cells.add(GridPoint(r, c))
        // Neck
        for (r in 3..7) {
            cells.add(GridPoint(r, 5))
            cells.add(GridPoint(r, 6))
        }
        // Body upper bout
        for (r in 8..10) for (c in 3..8) cells.add(GridPoint(r, c))
        // Waist
        for (c in 4..7) cells.add(GridPoint(11, c))
        // Body lower bout
        for (r in 12..15) for (c in 2..9) cells.add(GridPoint(r, c))
        return ShapeMask(ShapeType.GUITAR, rows, cols, cells)
    }

    // 18. Castle
    fun createCastle(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Main wall
        for (r in 6..13) for (c in 2..12) cells.add(GridPoint(r, c))
        // Left tower
        for (r in 2..5) for (c in 2..4) cells.add(GridPoint(r, c))
        cells.add(GridPoint(1, 2)); cells.add(GridPoint(1, 4))
        // Right tower
        for (r in 2..5) for (c in 10..12) cells.add(GridPoint(r, c))
        cells.add(GridPoint(1, 10)); cells.add(GridPoint(1, 12))
        // Center crenels
        cells.add(GridPoint(5, 6)); cells.add(GridPoint(5, 8))
        return ShapeMask(ShapeType.CASTLE, rows, cols, cells)
    }

    // 19. Teapot
    fun createTeapot(): ShapeMask {
        val rows = 15
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        // Lid
        cells.add(GridPoint(2, 7)); cells.add(GridPoint(2, 8))
        for (c in 5..10) cells.add(GridPoint(3, c))
        // Body
        for (r in 4..11) for (c in 4..11) cells.add(GridPoint(r, c))
        // Spout (left)
        cells.add(GridPoint(4, 2)); cells.add(GridPoint(5, 3)); cells.add(GridPoint(6, 3))
        // Handle (right)
        for (r in 5..9) { cells.add(GridPoint(r, 13)); cells.add(GridPoint(r, 14)) }
        // Base
        for (c in 5..10) cells.add(GridPoint(12, c))
        return ShapeMask(ShapeType.TEAPOT, rows, cols, cells)
    }

    // 20. Owl
    fun createOwl(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Horns/ears
        cells.add(GridPoint(1, 4)); cells.add(GridPoint(1, 10))
        // Head
        for (r in 2..5) for (c in 3..11) cells.add(GridPoint(r, c))
        // Body
        for (r in 6..12) for (c in 4..10) cells.add(GridPoint(r, c))
        // Wings
        for (r in 6..10) { cells.add(GridPoint(r, 2)); cells.add(GridPoint(r, 3)); cells.add(GridPoint(r, 11)); cells.add(GridPoint(r, 12)) }
        // Perch branch
        for (c in 2..12) cells.add(GridPoint(13, c))
        return ShapeMask(ShapeType.OWL, rows, cols, cells)
    }

    // 21. Crescent Moon
    fun createMoon(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val d1 = (r - 7.0) * (r - 7.0) + (c - 7.0) * (c - 7.0)
                val d2 = (r - 6.0) * (r - 6.0) + (c - 9.0) * (c - 9.0)
                if (d1 <= 36.0 && d2 >= 20.0) {
                    cells.add(GridPoint(r, c))
                }
            }
        }
        return ShapeMask(ShapeType.MOON, rows, cols, cells)
    }

    // 22. Dragon
    fun createDragon(): ShapeMask {
        val rows = 16
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        // Head
        for (r in 1..4) for (c in 10..14) cells.add(GridPoint(r, c))
        cells.add(GridPoint(2, 15))
        // Long neck
        for (r in 4..8) for (c in 8..11) cells.add(GridPoint(r, c))
        // Body
        for (r in 8..12) for (c in 4..12) cells.add(GridPoint(r, c))
        // Wings
        for (r in 3..7) for (c in 2..7) cells.add(GridPoint(r, c))
        // Tail
        for (r in 12..14) {
            cells.add(GridPoint(r, 2))
            cells.add(GridPoint(r, 3))
        }
        cells.add(GridPoint(14, 1))
        return ShapeMask(ShapeType.DRAGON, rows, cols, cells)
    }

    // 23. Sports Car
    fun createCar(): ShapeMask {
        val rows = 14
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        // Roof & Cabin
        for (r in 4..7) for (c in 5..10) cells.add(GridPoint(r, c))
        // Hood & Trunk body
        for (r in 8..10) for (c in 1..14) cells.add(GridPoint(r, c))
        // Wheels
        for (c in 3..5) cells.add(GridPoint(11, c))
        for (c in 10..12) cells.add(GridPoint(11, c))
        return ShapeMask(ShapeType.CAR, rows, cols, cells)
    }

    // 24. Lotus
    fun createLotus(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Base petals
        for (c in 3..11) cells.add(GridPoint(12, c))
        for (c in 1..13) cells.add(GridPoint(11, c))
        // Center petal
        for (r in 3..10) for (c in 6..8) cells.add(GridPoint(r, c))
        cells.add(GridPoint(2, 7))
        // Left petal
        for (r in 5..10) for (c in 2..5) cells.add(GridPoint(r, c))
        cells.add(GridPoint(4, 3))
        // Right petal
        for (r in 5..10) for (c in 9..12) cells.add(GridPoint(r, c))
        cells.add(GridPoint(4, 11))
        return ShapeMask(ShapeType.LOTUS, rows, cols, cells)
    }

    // 25. Lion
    fun createLion(): ShapeMask {
        val rows = 16
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        // Mane
        for (r in 1..7) for (c in 8..14) cells.add(GridPoint(r, c))
        // Muzzle
        for (r in 4..6) for (c in 13..15) cells.add(GridPoint(r, c))
        // Body
        for (r in 7..11) for (c in 3..11) cells.add(GridPoint(r, c))
        // Legs
        for (r in 11..15) {
            cells.add(GridPoint(r, 4)); cells.add(GridPoint(r, 5))
            cells.add(GridPoint(r, 9)); cells.add(GridPoint(r, 10))
        }
        // Tail
        for (r in 5..8) cells.add(GridPoint(r, 2))
        cells.add(GridPoint(5, 1))
        return ShapeMask(ShapeType.LION, rows, cols, cells)
    }

    // 26. Skeleton Key
    fun createKey(): ShapeMask {
        val rows = 16
        val cols = 12
        val cells = mutableSetOf<GridPoint>()
        // Bow (ring handle at top)
        for (r in 0..4) for (c in 3..8) {
            if (r == 0 || r == 4 || c == 3 || c == 8) cells.add(GridPoint(r, c))
        }
        // Stem
        for (r in 5..14) {
            cells.add(GridPoint(r, 5)); cells.add(GridPoint(r, 6))
        }
        // Bit / teeth
        for (c in 7..9) {
            cells.add(GridPoint(12, c)); cells.add(GridPoint(14, c))
        }
        return ShapeMask(ShapeType.KEY, rows, cols, cells)
    }

    // 27. Compass
    fun createCompass(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Ring
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                val d = (r - 7.0) * (r - 7.0) + (c - 7.0) * (c - 7.0)
                if (d in 24.0..42.0) cells.add(GridPoint(r, c))
            }
        }
        // 4 pointers
        for (r in 2..12) cells.add(GridPoint(r, 7))
        for (c in 2..12) cells.add(GridPoint(7, c))
        return ShapeMask(ShapeType.COMPASS, rows, cols, cells)
    }

    // 28. Apollo Rocket
    fun createRocket(): ShapeMask {
        val rows = 16
        val cols = 13
        val cells = mutableSetOf<GridPoint>()
        // Nosecone
        cells.add(GridPoint(0, 6))
        for (c in 5..7) cells.add(GridPoint(1, c))
        // Fuselage
        for (r in 2..11) for (c in 4..8) cells.add(GridPoint(r, c))
        // Fins
        for (r in 9..13) {
            cells.add(GridPoint(r, 2)); cells.add(GridPoint(r, 3))
            cells.add(GridPoint(r, 9)); cells.add(GridPoint(r, 10))
        }
        // Exhaust flames
        for (r in 12..15) cells.add(GridPoint(r, 6))
        cells.add(GridPoint(13, 5)); cells.add(GridPoint(13, 7))
        return ShapeMask(ShapeType.ROCKET, rows, cols, cells)
    }

    // 29. Tortoise
    fun createTurtle(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Shell
        for (r in 3..11) for (c in 3..11) {
            if ((r - 7.0) * (r - 7.0) + (c - 7.0) * (c - 7.0) <= 20.0) {
                cells.add(GridPoint(r, c))
            }
        }
        // Head
        for (r in 0..2) for (c in 6..8) cells.add(GridPoint(r, c))
        // Flippers
        cells.add(GridPoint(2, 2)); cells.add(GridPoint(3, 2))
        cells.add(GridPoint(2, 12)); cells.add(GridPoint(3, 12))
        cells.add(GridPoint(11, 2)); cells.add(GridPoint(12, 2))
        cells.add(GridPoint(11, 12)); cells.add(GridPoint(12, 12))
        return ShapeMask(ShapeType.TURTLE, rows, cols, cells)
    }

    // 30. Phoenix
    fun createPhoenix(): ShapeMask {
        val rows = 16
        val cols = 16
        val cells = mutableSetOf<GridPoint>()
        // Wing arcs
        for (c in 0..15) {
            cells.add(GridPoint(4, c)); cells.add(GridPoint(5, c))
        }
        for (r in 2..10) {
            cells.add(GridPoint(r, 7)); cells.add(GridPoint(r, 8))
        }
        cells.add(GridPoint(1, 8)) // Head
        // Flaming tail
        for (r in 11..15) {
            cells.add(GridPoint(r, 5)); cells.add(GridPoint(r, 7)); cells.add(GridPoint(r, 9))
        }
        return ShapeMask(ShapeType.PHOENIX, rows, cols, cells)
    }

    // 31. Bunny
    fun createBunny(): ShapeMask {
        val rows = 15
        val cols = 13
        val cells = mutableSetOf<GridPoint>()
        // Ears
        for (r in 0..3) { cells.add(GridPoint(r, 4)); cells.add(GridPoint(r, 8)) }
        // Head
        for (r in 4..7) for (c in 3..9) cells.add(GridPoint(r, c))
        // Body
        for (r in 8..13) for (c in 2..10) cells.add(GridPoint(r, c))
        // Tail
        cells.add(GridPoint(11, 11))
        return ShapeMask(ShapeType.BUNNY, rows, cols, cells)
    }

    // 32. Coffee
    fun createCoffee(): ShapeMask {
        val rows = 14
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Steam
        cells.add(GridPoint(0, 5)); cells.add(GridPoint(1, 6))
        cells.add(GridPoint(0, 8)); cells.add(GridPoint(1, 9))
        // Mug body
        for (r in 3..10) for (c in 3..10) cells.add(GridPoint(r, c))
        // Handle
        for (r in 4..8) cells.add(GridPoint(r, 12))
        cells.add(GridPoint(4, 11)); cells.add(GridPoint(8, 11))
        // Saucer
        for (c in 1..12) cells.add(GridPoint(12, c))
        return ShapeMask(ShapeType.COFFEE, rows, cols, cells)
    }

    // 33. Jet Plane
    fun createPlane(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Fuselage
        for (r in 0..14) { cells.add(GridPoint(r, 7)) }
        // Main Wings
        for (c in 1..13) cells.add(GridPoint(7, c))
        for (c in 3..11) cells.add(GridPoint(8, c))
        // Tail wings
        for (c in 4..10) cells.add(GridPoint(13, c))
        return ShapeMask(ShapeType.PLANE, rows, cols, cells)
    }

    // 34. Diamond Ring
    fun createRing(): ShapeMask {
        val rows = 15
        val cols = 13
        val cells = mutableSetOf<GridPoint>()
        // Gem on top
        for (r in 0..3) for (c in 4..8) cells.add(GridPoint(r, c))
        // Ring loop
        for (r in 4..14) for (c in 2..10) {
            val d = (r - 9.0) * (r - 9.0) + (c - 6.0) * (c - 6.0)
            if (d in 12.0..25.0) cells.add(GridPoint(r, c))
        }
        return ShapeMask(ShapeType.RING, rows, cols, cells)
    }

    // 35. Trophy
    fun createTrophy(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Cup rim & bowl
        for (r in 2..7) for (c in 4..10) cells.add(GridPoint(r, c))
        // Handles
        for (r in 3..6) { cells.add(GridPoint(r, 2)); cells.add(GridPoint(r, 12)) }
        // Stem
        for (r in 8..11) { cells.add(GridPoint(r, 7)) }
        // Pedestal
        for (c in 3..11) cells.add(GridPoint(12, c))
        for (c in 2..12) cells.add(GridPoint(13, c))
        return ShapeMask(ShapeType.TROPHY, rows, cols, cells)
    }

    // 36. Palm Tree Island
    fun createPalm(): ShapeMask {
        val rows = 15
        val cols = 15
        val cells = mutableSetOf<GridPoint>()
        // Fronds
        for (c in 1..13) cells.add(GridPoint(2, c))
        cells.add(GridPoint(1, 4)); cells.add(GridPoint(1, 10))
        cells.add(GridPoint(3, 2)); cells.add(GridPoint(3, 12))
        // Trunk curving slightly
        for (r in 3..12) {
            val col = 6 + (r - 3) / 4
            cells.add(GridPoint(r, col))
            cells.add(GridPoint(r, col + 1))
        }
        // Island sand
        for (c in 3..13) cells.add(GridPoint(13, c))
        for (c in 4..12) cells.add(GridPoint(14, c))
        return ShapeMask(ShapeType.PALM, rows, cols, cells)
    }
}
