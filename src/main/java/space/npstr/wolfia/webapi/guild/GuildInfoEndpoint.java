/*
 * Copyright (C) 2016-2020 the original author or authors
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

package space.npstr.wolfia.webapi.guild;

import java.util.List;
import javax.annotation.Nullable;
import net.dv8tion.jda.api.sharding.ShardManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import space.npstr.wolfia.db.type.OAuth2Scope;
import space.npstr.wolfia.domain.guild.GuildInfo;
import space.npstr.wolfia.domain.guild.RemoteGuildService;
import space.npstr.wolfia.webapi.WebUser;

@RestController
@RequestMapping("/api")
public class GuildInfoEndpoint extends GuildEndpoint {

    private final RemoteGuildService remoteGuildService;

    public GuildInfoEndpoint(RemoteGuildService remoteGuildService, ShardManager shardManager) {
        super(remoteGuildService, shardManager);
        this.remoteGuildService = remoteGuildService;
    }

    @GetMapping("/guild/{guildId}")
    public ResponseEntity<GuildInfo> getGuild(@PathVariable long guildId, @Nullable WebUser user) {
        WebContext context = assertGuildAccess(user, guildId);

        return this.remoteGuildService.asUser(context.user)
                .fetchGuild(guildId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/guilds")
    public ResponseEntity<List<GuildInfo>> getGuilds(@Nullable WebUser user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        if (!user.hasScope(OAuth2Scope.GUILDS)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(
                this.remoteGuildService.asUser(user)
                        .fetchAllGuilds()
        );
    }
}
