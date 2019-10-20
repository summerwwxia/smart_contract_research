/*
 * Copyright (C) 2016-2019 Dennis Neufeld
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

package space.npstr.wolfia.domain.stats;

import org.immutables.value.Value.Immutable;

@Immutable
@StatsStyle
public interface WinStats {

    /**
     * @return the player size that these win stats belong to
     */
    int playerSize();

    /**
     * @return total games with this player size
     */
    long totalGames();

    /**
     * @return goodie wins with this player size
     */
    long goodieWins();

    /**
     * @return baddie wins with this player size
     */
    long baddieWins();

}
