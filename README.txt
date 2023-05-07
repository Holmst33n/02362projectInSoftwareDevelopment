===============================================================================

Roborally

===============================================================================

Installation:
-------------

1. Clone the repository or download the game.
2. Compile the game.
3. Make sure you have MariaDB installed and running on your computer; see
https://mariadb.com/kb/en/getting-installing-and-upgrading-mariadb/
4. In the "Connector" class in the dal package in the project, change the
"Password" string to whichever password you chose to set up mariadb.
5. Open the command prompt and navigate to your MariaDB installation folder.
Should be C:\Program Files\MariaDB 10.6\bin or similar.
6. Type 'mysql -u root -p', and then enter your password.
7. Type 'CREATE DATABASE pisu;'.
8. Run the game by running the "StartRoboRally" class in the view package
in the project structure.


Gameplay:
---------

1. Start the game by clicking "File".
2. Follow the prompts and start a new game. You can choose the amount of
players and which board you want to play on.
3. Play the RoboRally game. Rules can be found here:
https://www.fgbradleys.com/rules/rules4/Robo%20Rally%20-%20rules.pdf
4. The game can be saved and loaded later. In the File menu, you can choose
to either stop, save or exit the game.

Noteworthy:
-----------

The scale of the game window can be changed in the "SpaceView" in the view
package in the in the project. Change SPACE_WIDTH and SPACE_HEIGHT.

Credits:
--------

- Alexander Szabo - s205793
- Joes Nicolaisen - s224580
- Johan Holmsteen - S224568
- Mikkel Nørgaard - S224562
- FGBradleys, RoboRally rules