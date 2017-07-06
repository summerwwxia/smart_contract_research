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

package space.npstr.wolfia.db.entity.stats;

import org.hibernate.annotations.ColumnDefault;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import space.npstr.wolfia.db.DbWrapper;
import space.npstr.wolfia.game.Games;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by napster on 30.05.17.
 * <p>
 * Describe a game that happened
 * <p>
 */
@Entity
@Table(name = "stats_game")
public class GameStats implements Serializable {

    private static final long serialVersionUID = -577030472501735570L;
    private static final Logger log = LoggerFactory.getLogger(GameStats.class);

    //this is pretty much an auto incremented id generator starting by 1 and going 1 upwards
    //there are no hard guarantees that there wont be any gaps, or that they will be in any order in the table
    //that's good enough for our use case though (giving games an "easy" to remember number to request replays and stats
    //later, and passively showing off how many games the bot has done)
    private static final String gameIdSeqName = "stats_game_game_id_seq";
    @Id
    @SequenceGenerator(name = gameIdSeqName, sequenceName = gameIdSeqName, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = gameIdSeqName)
    @Column(name = "game_id", updatable = false)
    private long gameId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "game", orphanRemoval = true)
    @Column(name = "starting_teams")
    private Set<TeamStats> startingTeams = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "game", orphanRemoval = true)
    @Column(name = "actions")
    private List<ActionStats> actions = new ArrayList<>();

    @Column(name = "start_time")
    private long startTime;

    @Column(name = "end_time")
    private long endTime;

    @Column(name = "guild_id")
    private long guildId;

    //name of the guild at the time of creation
    @Column(name = "guild_name")
    private String guildName;

    @Column(name = "channel_id")
    private long channelId;

    @Column(name = "channel_name")
    private String channelName;

    @Column(name = "game_type")
    private String gameType;

    @Column(name = "game_mode")
    private String gameMode;

    @Column(name = "player_size")
    @ColumnDefault(value = "-1") //todo remove
    private int playerSize;

    //todo remove this code
    public static String evalMigrate() {
        final List<Long> gameIds = DbWrapper.selectJPQLQuery("SELECT gameId FROM GameStats", Long.class);
        int toMigrate = 0;
        int failed = 0;
        for (final long id : gameIds) {
            try {
                final GameStats game = DbWrapper.loadSingleGameStats(id);

                if (game.playerSize > -1) continue;
                toMigrate++;

                int sum = 0;
                for (final TeamStats team : game.getStartingTeams()) {
                    sum += team.getPlayers().size();
                    if (team.getTeamSize() < 0) {
                        team.setTeamSize(team.getPlayers().size());
                    }

                    for (final PlayerStats player : team.getPlayers()) {
                        player.setAlignment(team.getAlignment());
                    }
                }
                game.playerSize = sum;

                for (final ActionStats action : game.actions) {
                    action.setPhase(ActionStats.Phase.DAY);
                }

                DbWrapper.merge(game);
            } catch (final Exception e) {
                log.warn("Failed to migrate game #{} ", id, e);
                failed++;
            }
        }
        return "Successfully migrated " + (toMigrate - failed) + " / " + toMigrate + " / " + gameIds.size() + " total game stats";
    }

    public GameStats(final long guildId, final String guildName, final long channelId, final String channelName,
                     final Games gameType, final String gameMode, final int playerSize) {
        this.guildId = guildId;
        this.guildName = guildName;
        this.channelId = channelId;
        this.channelName = channelName;
        this.startTime = System.currentTimeMillis();
        this.gameType = gameType.name();
        this.gameMode = gameMode;
        this.playerSize = playerSize;
    }

    public void addAction(final ActionStats action) {
        this.actions.add(action);
    }

    public void addTeam(final TeamStats team) {
        this.startingTeams.add(team);
    }

    //do not use the autogenerated id, it will only be set after persisting
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (this.guildId ^ (this.guildId >>> 32));
        result = prime * result + (int) (this.channelId ^ (this.channelId >>> 32));
        result = prime * result + (int) (this.startTime ^ (this.startTime >>> 32));
        return result;
    }

    //do not compare the autogenerated id, it will only be set after persisting
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof GameStats)) {
            return false;
        }
        final GameStats g = (GameStats) obj;
        return this.startTime == g.startTime && this.guildId == g.guildId && this.channelId == g.channelId;
    }

    //########## boilerplate code below
    GameStats() {
    }

    public long getGameId() {
        return this.gameId;
    }

    public void setGameId(final long gameId) {
        this.gameId = gameId;
    }

    public Set<TeamStats> getStartingTeams() {
        return this.startingTeams;
    }

    public void setStartingTeams(final Set<TeamStats> startingTeams) {
        this.startingTeams = startingTeams;
    }

    public List<ActionStats> getActions() {
        return this.actions;
    }

    public void setActions(final List<ActionStats> actions) {
        this.actions = actions;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final long endTime) {
        this.endTime = endTime;
    }

    public long getGuildId() {
        return this.guildId;
    }

    public void setGuildId(final long guildId) {
        this.guildId = guildId;
    }

    public String getGuildName() {
        return this.guildName;
    }

    public void setGuildName(final String guildName) {
        this.guildName = guildName;
    }

    public long getChannelId() {
        return this.channelId;
    }

    public void setChannelId(final long channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return this.channelName;
    }

    public void setChannelName(final String channelName) {
        this.channelName = channelName;
    }

    public Games getGameType() {
        return Games.valueOf(this.gameType);
    }

    public void setGameType(final Games game) {
        this.gameType = game.name();
    }

    public String getGameMode() {
        return this.gameMode;
    }

    public void setGameMode(final String gameMode) {
        this.gameMode = gameMode;
    }

    public int getPlayerSize() {
        return this.playerSize;
    }

    public void setPlayerSize(final int playerSize) {
        this.playerSize = playerSize;
    }
}
