/*
 * Copyright (C) 2017 Dennis Neufeld
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package space.npstr.wolfia.commands.util;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.npstr.wolfia.Config;
import space.npstr.wolfia.Wolfia;
import space.npstr.wolfia.commands.CommandParser;
import space.npstr.wolfia.commands.ICommand;
import space.npstr.wolfia.db.DbWrapper;
import space.npstr.wolfia.db.entity.stats.ActionStats;
import space.npstr.wolfia.db.entity.stats.GameStats;
import space.npstr.wolfia.db.entity.stats.TeamStats;
import space.npstr.wolfia.utils.Emojis;
import space.npstr.wolfia.utils.IllegalGameStateException;
import space.npstr.wolfia.utils.TextchatUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by napster on 03.06.17.
 * <p>
 * Shows replays of games that are over
 */
public class ReplayCommand implements ICommand {

    public static final String COMMAND = "replay";

    private static final Logger log = LoggerFactory.getLogger(ReplayCommand.class);

    @Override
    public void execute(final CommandParser.CommandContainer commandInfo) throws IllegalGameStateException {

        final MessageReceivedEvent e = commandInfo.event;
        if (commandInfo.args.length < 1) {
            Wolfia.handleOutputMessage(e.getTextChannel(), "%s", help());
            return;
        }

        final long gameId;
        try {
            gameId = Long.valueOf(commandInfo.args[0].replaceAll("#", ""));
        } catch (final NumberFormatException ex) {
            Wolfia.handleOutputMessage(e.getTextChannel(), "%s", help());
            return;
        }

        final GameStats gameStats = DbWrapper.loadSingleGameStats(gameId);

        if (gameStats == null) {
            Wolfia.handleOutputMessage(e.getTextChannel(), "%s, there is no such game in the database.",
                    e.getAuthor().getAsMention());
            return;
        }
        final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss z").withZone(ZoneId.of("UTC"));

        final EmbedBuilder eb = new EmbedBuilder();

        //1. post summary like game, mode, players, roles
        eb.setTitle("**Game #" + gameStats.getGameId() + "**");
        eb.setDescription(gameStats.getGameType().textRep + " " + gameStats.getGameMode());
        eb.addField("Game started", dtf.format(Instant.ofEpochMilli(gameStats.getStartTime())), true);

        gameStats.getStartingTeams().forEach(team ->
                eb.addField(team.getAlignment().textRep,
                        String.join(", ",
                                team.getPlayers().stream().map(player -> "`" + player.getNickname() + "`").collect(Collectors.toList())),
                        true)
        );


        //2. post the actions
        StringBuilder actions = new StringBuilder();
        String fieldTitle = "Actions";
        for (final ActionStats action : gameStats.getActions()) {
            final String actionStr = action.toString();

            //split into several fields if needed
            if (actions.length() + actionStr.length() + 1 > MessageEmbed.VALUE_MAX_LENGTH) {
                eb.addField(fieldTitle, actions.toString(), false);
                fieldTitle = "";//empty title for following fields
                actions = new StringBuilder(); //reset the summary string
            }
            actions.append(actionStr).append("\n");
        }
        eb.addField(fieldTitle, actions.toString(), false);

        //3. post the winners
        eb.addField("Game ended", dtf.format(Instant.ofEpochMilli(gameStats.getEndTime())), true);
        eb.addField("Game length", TextchatUtils.formatMillis(gameStats.getEndTime() - gameStats.getStartTime()), true);

        String winText = "";
        final Optional<TeamStats> winners = gameStats.getStartingTeams().stream().filter(TeamStats::isWinner).findFirst();
        if (!winners.isPresent()) {
            //shouldn't happen lol
            log.error("Game #{} has no winning team in the data", gameId);
            winText = "Game has no winning team " + Emojis.WOLFTHINK + "\nReplay must be borked. Error has been reported.";
        } else {
            final TeamStats winningTeam = winners.get();
            winText = "**Team " + winningTeam.getAlignment().textRep + " wins the game!**";
        }
        eb.addField("Winners", winText, true);

        Wolfia.handleOutputEmbed(e.getTextChannel(), eb.build());

//        final StringBuilder out = new StringBuilder("**Game #").append(gameStats.getGameId()).append("** ");
//        //1. post summary like game, mode, players, roles
//        out.append(gameStats.getGameType().textRep);
//        out.append("\n**Mode**: ").append(gameStats.getGameMode());
//        out.append("\n**Started**: ").append(dtf.format(Instant.ofEpochMilli(gameStats.getStartTime())));
//        gameStats.getStartingTeams().forEach(team -> out
//                .append("\n**")
//                .append(team.getAlignment().textRep)
//                .append("**: ")
//                .append(String.join(", ", team.getPlayers().stream().map(player -> "`" + player.getNickname() + "`").collect(Collectors.toList())))
//        );
//
//        //2. post the actions
//        gameStats.getActions().forEach(actionStats -> out.append("\n").append(actionStats.toString()));
//
//        //3. post the winners
//        out.append("\n**Game ended**: ").append(dtf.format(Instant.ofEpochMilli(gameStats.getEndTime())));
//        out.append("\n**Game length**: ").append(TextchatUtils.formatMillis(gameStats.getEndTime() - gameStats.getStartTime()));
//        final Optional<TeamStats> winners = gameStats.getStartingTeams().stream().filter(TeamStats::isWinner).findFirst();
//        if (!winners.isPresent()) {
//            //shouldn't happen lol
//            out.append("\nGame has no winning team ").append(Emojis.WOLFTHINK)
//                    .append("\nReplay must be borked. Error has been reported.");
//            log.error("Game #{} has no winning team in the data", gameId);
//        } else {
//            final TeamStats winningTeam = winners.get();
//            out.append("\n**Team ").append(winningTeam.getAlignment().textRep).append(" wins the game!**");
//        }
//
//        Wolfia.handleOutputMessage(e.getTextChannel(), "%s", out);
    }


    @Override
    public String help() {
        return "```usage: " + Config.PREFIX + COMMAND + " #gameid\nShow the replay of a game.```";
    }
}