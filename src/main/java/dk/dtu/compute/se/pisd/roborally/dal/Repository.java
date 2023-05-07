/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.dal;

import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ...
 *
 * In here we have all methods to interact with the database.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Joes Nicolaisen, s224564
 * @author Johan Holmsteen, s224568
 *
 */
class Repository implements IRepository {
	
	private static final String GAME_GAMEID = "gameID";

	private static final String GAME_NAME = "name";
	
	private static final String GAME_CURRENTPLAYER = "currentPlayer";

	private static final String GAME_PHASE = "phase";

	private static final String GAME_STEP = "step";

	private static final String GAME_BOARDNAME = "boardName";
	
	private static final String PLAYER_PLAYERID = "playerID";
	
	private static final String PLAYER_NAME = "name";

	private static final String PLAYER_COLOUR = "colour";
	
	private static final String PLAYER_GAMEID = "gameID";
	
	private static final String PLAYER_POSITION_X = "positionX";

	private static final String PLAYER_POSITION_Y = "positionY";

	private static final String PLAYER_HEADING = "heading";

	private static final String PLAYER_CHECKPOINT_NUMBER = "checkpointNumber";

	private static final String COMMANDCARD_GAMEID = "gameID";

	private static final String COMMANDCARD_PLAYERID = "playerID";

	private static final String COMMANDCARD_COMMANDCARDID = "commandcardID";

	private static final String COMMANDCARD_TYPE = "Type";

	private static final String COMMANDCARD_NUMBER = "Number";

	private static final int PROGRAMMING_CARD = 0;
	private static final int HAND_CARD = 1;

	private Connector connector;
	
	Repository(Connector connector){
		this.connector = connector;
	}

	/**
	 * ...
	 *
	 * Creates a new game record in the database for the specified game object.
	 *
	 * @param game the game object to create a new record for
	 * @return true if the game record was created successfully, false otherwise
	 *
	 * @author Ekkart Kindler, ekki@dtu.dk
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
	@Override
	public boolean createGameInDB(Board game) {
		if (game.getGameId() == null) {
			Connection connection = connector.getConnection();
			try {
				connection.setAutoCommit(false);

				PreparedStatement ps = getInsertGameStatementRGK();
				ps.setString(1, "Date: " +  new Date());
				ps.setString(2, game.getBoardName());
				ps.setNull(3, Types.TINYINT); // game.getPlayerNumber(game.getCurrentPlayer())); is inserted after players!
				ps.setInt(4, game.getPhase().ordinal());
				ps.setInt(5, game.getStep());
				
				int affectedRows = ps.executeUpdate();
				ResultSet generatedKeys = ps.getGeneratedKeys();
				if (affectedRows == 1 && generatedKeys.next()) {
					game.setGameId(generatedKeys.getInt(1));
				}
				generatedKeys.close();

				createPlayersInDB(game);
				createCommandCardsInDB(game);

				ps = getSelectGameStatementU();
				ps.setInt(1, game.getGameId());

				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
					rs.updateRow();
				}
				rs.close();

				connection.commit();
				connection.setAutoCommit(true);
				return true;
			} catch (SQLException e) {
				e.printStackTrace();
				System.err.println("Some DB error");
				
				try {
					connection.rollback();
					connection.setAutoCommit(true);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			System.err.println("Game cannot be created in DB, since it has a game id already!");
		}
		return false;
	}

	/**
	 * ...
	 *
	 * Updates the specified game in the database with the current game information.
	 *
	 * @param game the Board object representing the current game to be updated in the database.
	 * @return true if the game was successfully updated in the database, false otherwise.
	 *
	 * @author Ekkart Kindler, ekki@dtu.dk
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
	@Override
	public boolean updateGameInDB(Board game) {
		assert game.getGameId() != null;
		
		Connection connection = connector.getConnection();
		try {
			connection.setAutoCommit(false);

			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, game.getGameId());
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				rs.updateInt(GAME_CURRENTPLAYER, game.getPlayerNumber(game.getCurrentPlayer()));
				rs.updateInt(GAME_PHASE, game.getPhase().ordinal());
				rs.updateInt(GAME_STEP, game.getStep());
				rs.updateRow();
			}
			rs.close();

			updatePlayersInDB(game);

            connection.commit();
            connection.setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Some DB error");
			
			try {
				connection.rollback();
				connection.setAutoCommit(true);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * ...
	 *
	 * Loads a game from the database with the specified gameID and returns it as a Board object.
	 *
	 * @param id the ID of the game to load
	 * @return the loaded game as a Board object or null if an error occurs
	 *
	 * @author Ekkart Kindler, ekki@dtu.dk
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
	@Override
	public Board loadGameFromDB(int id) {
		Board game;
		try {
			PreparedStatement ps = getSelectGameStatementU();
			ps.setInt(1, id);
			
			ResultSet rs = ps.executeQuery();
			int playerNo = -1;
			if (rs.next()) {
				game = LoadBoard.loadBoard(rs.getString(GAME_BOARDNAME));
				if (game == null) {
					return null;
				}
				playerNo = rs.getInt(GAME_CURRENTPLAYER);
				game.setPhase(Phase.values()[rs.getInt(GAME_PHASE)]);
				game.setStep(rs.getInt(GAME_STEP));
			} else {
				return null;
			}
			rs.close();

			game.setGameId(id);			
			loadPlayersFromDB(game);

			if (playerNo >= 0 && playerNo < game.getPlayersNumber()) {
				game.setCurrentPlayer(game.getPlayer(playerNo));
			} else {
				return null;
			}
			return game;
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Some DB error");
		}
		return null;
	}
	
	@Override
	public List<GameInDB> getGames() {
		List<GameInDB> result = new ArrayList<>();
		try {
			PreparedStatement ps = getSelectGameIdsStatement();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(GAME_GAMEID);
				String name = rs.getString(GAME_NAME);
				result.add(new GameInDB(id,name));
			}
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;		
	}

	private void createPlayersInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());
		
		ResultSet rs = ps.executeQuery();
		for (int i = 0; i < game.getPlayersNumber(); i++) {
			Player player = game.getPlayer(i);
			rs.moveToInsertRow();
			rs.updateInt(PLAYER_GAMEID, game.getGameId());
			rs.updateInt(PLAYER_PLAYERID, i);
			rs.updateString(PLAYER_NAME, player.getName());
			rs.updateString(PLAYER_COLOUR, player.getColor());
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.updateInt(PLAYER_CHECKPOINT_NUMBER, player.getCurrentCheckpoint());
			rs.insertRow();
		}

		rs.close();
	}

	/**
	 * ...
	 *
	 * Creates and stores the command cards for each player in the database.
	 *
	 * @param game The Board object representing the current game.
	 * @throws SQLException if there is an error accessing the database
	 *
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
private void createCommandCardsInDB(Board game) throws SQLException {
	PreparedStatement ps = getSelectCommandCardsStatementU();
	ps.setInt(1, game.getGameId());

	ResultSet rs = ps.executeQuery();
	for (int i = 0; i < game.getPlayersNumber(); i++) {
		Player player = game.getPlayer(i);
		for (int j = 0; j < player.getProgram().length; j++) {
			rs.moveToInsertRow();
			rs.updateInt(COMMANDCARD_GAMEID, game.getGameId());
			rs.updateInt(COMMANDCARD_PLAYERID, i);
			rs.updateInt(COMMANDCARD_TYPE, PROGRAMMING_CARD);
			rs.updateInt(COMMANDCARD_NUMBER, j);
			if (player.getProgramField(j).getCard() != null) {
				rs.updateInt(COMMANDCARD_COMMANDCARDID, player.getProgramField(j).getCard().command.ordinal());
			}
			rs.insertRow();
		}

		for (int j = 0; j < player.getCards().length; j++) {
			rs.moveToInsertRow();
			rs.updateInt(COMMANDCARD_GAMEID, game.getGameId());
			rs.updateInt(COMMANDCARD_PLAYERID, i);
			rs.updateInt(COMMANDCARD_TYPE, HAND_CARD);
			rs.updateInt(COMMANDCARD_NUMBER, j);
			if (player.getCardField(j).getCard() != null) {
				rs.updateInt(COMMANDCARD_COMMANDCARDID, player.getCardField(j).getCard().command.ordinal());
			}
			rs.insertRow();
		}
	}
	rs.close();
}

	/**
	 * ...
	 *
	 * Loads the commandCards for each player of a single game. Writes them
	 * to each player's command card arrays
	 *
	 * @param game The Board object representing the current game.
	 * @throws SQLException if there is an error executing the SQL statement.
	 *
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
private void loadCommandCardsFromDB(Board game) throws SQLException {
	PreparedStatement ps = getSelectCommandCardsStatement();
	ps.setInt(1, game.getGameId());

	ResultSet rs = ps.executeQuery();
	while (rs.next()) {
			int playerID = rs.getInt(COMMANDCARD_PLAYERID);
			int type = rs.getInt(COMMANDCARD_TYPE);
			int number = rs.getInt(COMMANDCARD_NUMBER);
			Object obj = rs.getObject(COMMANDCARD_COMMANDCARDID);
			if(obj instanceof Integer) {
				Command command = Command.values()[(Integer) obj];
				CommandCard commandCard = new CommandCard(command);
				CommandCardField commandCardField = new CommandCardField(game.getPlayer(playerID));
				commandCardField.setCard(commandCard);
				if(type == PROGRAMMING_CARD){
					game.getPlayer(playerID).setProgramField(commandCardField, number);
				} else {
					game.getPlayer(playerID).setCardField(commandCardField, number);
				}
			}

		}
	}

	/**
	 * ...
	 *
	 * Loads each player from a specific game from the database.
	 * Proceeds to run loadCommandCardsFromDB
	 *
	 * @param game the game board to load the players into
	 * @throws SQLException if there is an error executing the SQL query or accessing the database
	 *
	 * @author Ekkart Kindler, ekki@dtu.dk
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
	private void loadPlayersFromDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersASCStatement();
		ps.setInt(1, game.getGameId());
		
		ResultSet rs = ps.executeQuery();
		int i = 0;
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			if (i++ == playerId) {
				String name = rs.getString(PLAYER_NAME);
				String colour = rs.getString(PLAYER_COLOUR);
				Player player = new Player(game, colour ,name);
				game.addPlayer(player);
				int x = rs.getInt(PLAYER_POSITION_X);
				int y = rs.getInt(PLAYER_POSITION_Y);
				player.setSpace(game.getSpace(x,y));
				int heading = rs.getInt(PLAYER_HEADING);
				player.setHeading(Heading.values()[heading]);
				int checkpointNumber = rs.getInt(PLAYER_CHECKPOINT_NUMBER);
				player.setCurrentCheckpointDB(checkpointNumber);

			} else {
				System.err.println("Game in DB does not have a player with id " + i +"!");
			}
		}
		loadCommandCardsFromDB(game);
		rs.close();
	}

	/**
	 * ...
	 *
	 * Used when saving a game, that is already present in the database.
	 * Updates each player to the database and the proceeds to run updateCommandCardsInDB()
	 *
	 * @param game the Board object representing the game state.
	 * @throws SQLException if there is an error while updating the player records in the database.
	 *
	 * @author Ekkart Kindler, ekki@dtu.dk
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
	private void updatePlayersInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectPlayersStatementU();
		ps.setInt(1, game.getGameId());
		
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int playerId = rs.getInt(PLAYER_PLAYERID);
			Player player = game.getPlayer(playerId);
			// rs.updateString(PLAYER_NAME, player.getName()); // not needed: player's names does not change
			rs.updateInt(PLAYER_POSITION_X, player.getSpace().x);
			rs.updateInt(PLAYER_POSITION_Y, player.getSpace().y);
			rs.updateInt(PLAYER_HEADING, player.getHeading().ordinal());
			rs.updateRow();
			updateCommandCardsInDB(game);
		}
		rs.close();
		
	}

	/**
	 * ...
	 *
	 * Used when saving a game, that is already present in the database.
	 * Updates the command cards in the database for a given game.
	 *
	 * @param game the Board object representing the game state.
	 * @throws SQLException if there is an error while updating the command
	 * card records in the database.
	 *
	 * @author Ekkart Kindler, ekki@dtu.dk
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 *
	 */
	private void updateCommandCardsInDB(Board game) throws SQLException {
		PreparedStatement ps = getSelectCommandCardsStatementU();
		ps.setInt(1, game.getGameId());
		ResultSet rs = ps.executeQuery();

		while(rs.next()) {
			Player player = game.getPlayer(rs.getInt(PLAYER_PLAYERID));
			int type = rs.getInt(COMMANDCARD_TYPE);
			int number = rs.getInt(COMMANDCARD_NUMBER);
			CommandCardField field;
			if (type == PROGRAMMING_CARD) {
				field = player.getProgramField(number);

			} else {
				field = player.getCardField(number);
			}

			CommandCard card = field.getCard();
			if(card == null) {
				rs.updateNull(COMMANDCARD_COMMANDCARDID);
			} else {
				rs.updateInt(COMMANDCARD_COMMANDCARDID, card.command.ordinal());
			}

			rs.updateRow();
		}
		rs.close();
	}


	private static final String SQL_INSERT_GAME =
			"INSERT INTO Game(name, boardName, currentPlayer, phase, step) VALUES (?, ?, ?, ?, ?)";

	private PreparedStatement insert_game_stmt = null;

	private PreparedStatement getInsertGameStatementRGK() {
		if (insert_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				insert_game_stmt = connection.prepareStatement(
						SQL_INSERT_GAME,
						Statement.RETURN_GENERATED_KEYS);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return insert_game_stmt;
	}

	private static final String SQL_SELECT_GAME =
			"SELECT * FROM Game WHERE gameID = ?";

	private PreparedStatement select_game_stmt = null;

	private PreparedStatement getSelectGameStatementU() {
		if (select_game_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_game_stmt = connection.prepareStatement(
						SQL_SELECT_GAME,
						ResultSet.TYPE_FORWARD_ONLY,
					    ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return select_game_stmt;
	}
		
	private static final String SQL_SELECT_PLAYERS =
			"SELECT * FROM Player WHERE gameID = ?";

	private PreparedStatement select_players_stmt = null;

	private PreparedStatement getSelectPlayersStatementU() {
		if (select_players_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_players_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return select_players_stmt;
	}

	private static final String SQL_SELECT_COMMANDCARDS =
			"SELECT * FROM Commandcards WHERE gameID = ?";

	private PreparedStatement select_commandcards_stmt = null;

	/**
	 * ...
	 *
	 * Returns a prepared statement to select command cards from the database.
	 * The method makes sure that the resulting result set from the prepared statement is
	 * updatable.
	 *
	 * @throws SQLException if there is an error while establishing a connection to the
	 * database or initializing the prepared statement.
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 */
	private PreparedStatement getSelectCommandCardsStatementU() {
		if (select_commandcards_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_commandcards_stmt = connection.prepareStatement(
						SQL_SELECT_COMMANDCARDS,
						ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_UPDATABLE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return select_commandcards_stmt;
	}


	private static final String SQL_SELECT_PLAYERS_ASC =
			"SELECT * FROM Player WHERE gameID = ? ORDER BY playerID ASC";
	
	private PreparedStatement select_players_asc_stmt = null;
	
	private PreparedStatement getSelectPlayersASCStatement() {
		if (select_players_asc_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				// This statement does not need to be updatable
				select_players_asc_stmt = connection.prepareStatement(
						SQL_SELECT_PLAYERS_ASC);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return select_players_asc_stmt;
	}
	
	private static final String SQL_SELECT_GAMES =
			"SELECT gameID, name FROM Game";
	
	private PreparedStatement select_games_stmt = null;
	
	private PreparedStatement getSelectGameIdsStatement() {
		if (select_games_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_games_stmt = connection.prepareStatement(
						SQL_SELECT_GAMES);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return select_games_stmt;
	}

	private PreparedStatement select_commandcard_stmt  = null;

	/**
	 * ...
	 *
	 * Returns a prepared statement to select command cards from the database.
	 *
	 * @throws SQLException if there is an error while establishing a connection
	 * to the database or initializing the prepared statement.
	 * @author Joes Nicolaisen, s224564
	 * @author Johan Holmsteen, s224568
	 */
	private PreparedStatement getSelectCommandCardsStatement() {
		if (select_commandcard_stmt == null) {
			Connection connection = connector.getConnection();
			try {
				select_commandcard_stmt = connection.prepareStatement(
						SQL_SELECT_COMMANDCARDS);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return select_commandcard_stmt;
	}
}
