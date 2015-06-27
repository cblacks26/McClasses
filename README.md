# VSkills
VSkills is a bukkit plugin that adds classes with skills that allow the player to level up and gain advantages, as well as earn money!
All of the Skill Classes and skills are completely customizable and allow the player to switch back and forth to whatever class they want to play on.

Skills:
  - Acrobatics - Fall Damaage
  - Archery -  Killing Entitites with a bow and arrow
  - Digging -  Digging blocks
  - Mining - Mining blocks
  - Woodcutting - Cutting down trees
  - Unarmed - Killing Entities with your bare hands

Creating a class is extremely easy. Here are a few steps to create a new class:

1) Open the config.yml

2) Navigate to the **Classes** section of the document
```
Classes:
  Assasin:
    Skills:
      ...
```
3) Create another Class name inside the **Classes** section, make sure it is indented by two spaces
```
Classes:
  Test Class Name:
    ...
```
4) Create a **skills** section inside the new class containing the list of the skills you want that class to have
```
Classes:
  Test Class Name:
    Skills:
      - Archery
      - Unarmed
      ...
```

Modifying the Skills is easy too

For the Skills: Digging, Mining, Woodcutting
- Make sure that the blocks you add have the correct names(Case doesn't matter). Otherwise ther will be an error on startup stating the problematic name
- Only the blocks included in this list will grant experience to the user
- Make sure there is a space between the ":" and your exp listed
- The Experience listed next to the block is how much experience will be gained on breaking each block
- Note that all 1.8 blocks are not compatible because bukkit doesn't support it

For the Skills: Archery, Unarmed, Weaponry
- Make sure that each of the Entities listed are correct (Case doesn't matter)
- These classes give a default 2.0 exp unless the entity is stated with an alternate exp

For Acrobatics:
- The ** Damage Modifier** is how much fall damage that will be subtracted from the total fall damage
```
Damage = Level * modifier
ex: damage subtracted = 10 * 0.07 = 0.7 
```
