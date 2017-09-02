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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Created by napster on 03.07.17.
 * <p>
 * Dump all kinds of discord/jvm/system/whatever related stats in here
 */
@Entity
@Table(name = "stats_general_bot")
public class GeneralBotStats implements Serializable {
    private static final long serialVersionUID = -5310532895894457569L;

    //just an autogenerated value
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    @Column(name = "user_count")
    private long userCount;

    @Column(name = "guild_count")
    private long guildCount;

    @Column(name = "shards_total")
    private int shardsTotal;

    @Column(name = "games_being_played")
    private int gamesBeingPlayed;

    @Column(name = "available_private_guilds_count")
    private int availablePrivateGuildsCount;

    @Column(name = "free_memory")
    private long freeMemory;

    @Column(name = "max_memory")
    private long maxMemory;

    @Column(name = "total_memory")
    private long totalMemory;

    @Column(name = "used_memory")
    private long usedMemory;

    @Column(name = "available_cores")
    private int availableCores;

    @Column(name = "average_load")
    private double averageLoad;

    @Column(name = "uptime")
    private long uptime;

    @Column(name = "time_stamp")
    private long timeStamp;

    public GeneralBotStats() {

    }

    public GeneralBotStats(final long userCount, final long guildCount, final int shardsTotal,
                           final int gamesBeingPlayed, final int availablePrivateGuildsCount, final long freeMemory,
                           final long maxMemory, final long totalMemory, final int availableCores,
                           final double averageLoad, final long uptime) {
        this.userCount = userCount;
        this.guildCount = guildCount;
        this.shardsTotal = shardsTotal;
        this.gamesBeingPlayed = gamesBeingPlayed;
        this.availablePrivateGuildsCount = availablePrivateGuildsCount;
        this.freeMemory = freeMemory;
        this.maxMemory = maxMemory;
        this.totalMemory = totalMemory;
        this.usedMemory = totalMemory - this.freeMemory;
        this.availableCores = availableCores;
        this.averageLoad = averageLoad;
        this.uptime = uptime;
        this.timeStamp = System.currentTimeMillis();

    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof GeneralBotStats)) {
            return false;
        }
        final GeneralBotStats gbs = (GeneralBotStats) obj;
        return this.id == gbs.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public long getId() {
        return this.id;
    }

    public long getUserCount() {
        return this.userCount;
    }

    public void setUserCount(final long userCount) {
        this.userCount = userCount;
    }

    public long getGuildCount() {
        return this.guildCount;
    }

    public void setGuildCount(final long guildCount) {
        this.guildCount = guildCount;
    }

    public int getShardsTotal() {
        return this.shardsTotal;
    }

    public void setShardsTotal(final int shardsTotal) {
        this.shardsTotal = shardsTotal;
    }

    public int getGamesBeingPlayed() {
        return this.gamesBeingPlayed;
    }

    public void setGamesBeingPlayed(final int gamesBeingPlayed) {
        this.gamesBeingPlayed = gamesBeingPlayed;
    }

    public int getAvailablePrivateGuildsCount() {
        return this.availablePrivateGuildsCount;
    }

    public void setAvailablePrivateGuildsCount(final int availablePrivateGuildsCount) {
        this.availablePrivateGuildsCount = availablePrivateGuildsCount;
    }

    public long getFreeMemory() {
        return this.freeMemory;
    }

    public void setFreeMemory(final long freeMemory) {
        this.freeMemory = freeMemory;
    }

    public long getMaxMemory() {
        return this.maxMemory;
    }

    public void setMaxMemory(final long maxMemory) {
        this.maxMemory = maxMemory;
    }

    public long getTotalMemory() {
        return this.totalMemory;
    }

    public void setTotalMemory(final long totalMemory) {
        this.totalMemory = totalMemory;
    }

    public long getUsedMemory() {
        return this.usedMemory;
    }

    public void setUsedMemory(final long usedMemory) {
        this.usedMemory = usedMemory;
    }

    public long getAvailableCores() {
        return this.availableCores;
    }

    public void setAvailableCores(final int availableCores) {
        this.availableCores = availableCores;
    }

    public double getAverageLoad() {
        return this.averageLoad;
    }

    public void setAverageLoad(final double averageLoad) {
        this.averageLoad = averageLoad;
    }

    public long getUptime() {
        return this.uptime;
    }

    public void setUptime(final long uptime) {
        this.uptime = uptime;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public void setTimeStamp(final long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
