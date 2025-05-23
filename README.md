Tento projekt je moje vlastní JavaFX hra ve stylu Terraria – 2D sandbox, ve kterém můžeš chodit, skákat, těžit a vyrábět předměty. Celé to stojí na jednoduchém enginu, který jsem napsal od nuly v Javě:

Nejdřív jsem vytvořil třídu GameApp, která spouští JavaFX, nastavuje okno (1720×820 px) a canvas, načítá pozadí, mapy a recepty, a pak popořádku skládá všechny komponenty dohromady: inventář, hráče, kameru, správce úrovní, UI, ukládání a vstup. Nakonec zavolá GameLoop, který každým snímkem počítá časový rozdíl, volá update(dt) a pak render(), aby vše plynule jelo.

Mapy (soubor map1.txt, map2.txt …) se parsují tak, že první blok řádek stejných délek definuje dlaždice přes ASCII znaky, a následně řádky jako ITEM baton 10 5 nebo NPC bro 15 8 určují, kde se vygenerují sběratelné předměty a postavičky. Když hráč opustí okraj, LevelManager prostě přehodí na další či předchozí mapu a posune ho na protější konec.

Vstupy klávesnicí a myší řeší InputHandler – WASD nebo šipky hýbou hráčem, mezerník skáče, E spustí dialog s nejbližším NPC, levé tlačítko myši těží dlaždici, pravé ji zase položí. Dialogy NPC se zobrazí jako poloprůhledné okno dole a postupně cyklí předem zadané texty.

Za craftování odpovídá CraftingManager s recepty načtenými z recipes.txt – když máš v inventáři dost ingrediencí, recept „zabuguje“ a přidá výsledný předmět. UI pak nakreslí inventář vlevo nahoře, dialogy, menu pauzy (Resume, Save, Exit) i okno craftingu se seznamem receptů a ingrediencí.

Player je třída, kde jsem řešil fyziku (gravitace, skoky, pohyb, kolize), animace (idle, běh, skok a varianta s vybaveným žezlem) a vykreslování. NPC má obyčejnou stojací animaci a jednoduchý mechanizmus otevírání a posunu řádků dialogu, když se přiblížíš a zmáčkneš E.

Na ukládání je SaveLoadManager, co zapisuje inventář i pozici a aktuální úroveň do textových souborů, aby ses mohl vrátit tam, kde jsi skončil. Všechny třídy jsou pokryté základní Javadoc dokumentací v angličtině a české komentáře tam doplňují detaily.

Projekt je modulární – používá Java 11+ s modulem org.example.game, JavaFX pro grafiku, apiguardian-api a JUnit Jupiter pro testování. Zdrojové mapy, obrázky a recepty jsou v resources. Cílem bylo vyzkoušet si stavbu vlastního herního enginu v Javě a naučit se, jak skloubit renderování, vstup, ukládání, UI i základní herní logiku do jednoho fungujícího celku.