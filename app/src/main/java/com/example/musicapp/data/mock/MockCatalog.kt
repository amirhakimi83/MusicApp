package com.example.musicapp.data.mock

import com.example.musicapp.domain.model.Artist
import com.example.musicapp.domain.model.Conversation
import com.example.musicapp.domain.model.Playlist
import com.example.musicapp.domain.model.PlaylistCategory
import com.example.musicapp.domain.model.Song
import com.example.musicapp.domain.model.User

/**
 * In-memory catalog used while there is no real backend.
 *
 * - Audio: real, royalty-free MP3s (SoundHelix) so playback actually works.
 * - Covers/avatars: deterministic real images (picsum.photos by seed).
 *
 * Generates 50+ songs to satisfy the spec's minimum content requirement.
 */
object MockCatalog {

    private const val COVER = "https://picsum.photos/seed"

    /** 16 real, streamable royalty-free tracks; cycled across the catalog. */
    private val audioPool: List<String> = (1..16).map {
        "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-$it.mp3"
    }

    private val artistNames = listOf(
        "Aria Nova", "The Midnight Keys", "Solstice", "Kian Rey", "Nova Bloom",
        "Echo Park", "Delaram", "Blue Meridian", "Sahar Vibe", "Lunar Tide",
        "Parsa Beats", "Velvet Horizon",
    )

    val artists: List<Artist> = artistNames.mapIndexed { i, name ->
        Artist(
            id = "artist_${i + 1}",
            name = name,
            imageUrl = "$COVER/artist${i + 1}/400/400",
            followerCount = 1_000 + (i * 7_919) % 90_000,
            bio = "Independent artist crafting original sounds.",
        )
    }

    private val titleAdjectives = listOf(
        "Midnight", "Golden", "Electric", "Velvet", "Neon", "Silent", "Crimson",
        "Endless", "Frozen", "Wild", "Lonely", "Cosmic", "Fading", "Broken",
        "Radiant", "Distant", "Sacred", "Restless",
    )
    private val titleNouns = listOf(
        "Horizon", "Echoes", "Dreams", "Rivers", "Skyline", "Embers", "Waves",
        "Shadows", "Lights", "Memories", "Storm", "Silhouette",
    )

    val songs: List<Song> = List(54) { i ->
        val artist = artists[i % artists.size]
        val title = "${titleAdjectives[i % titleAdjectives.size]} " +
            titleNouns[(i / titleAdjectives.size) % titleNouns.size]
        Song(
            id = "song_${i + 1}",
            title = title,
            artistName = artist.name,
            artistId = artist.id,
            coverImageUrl = "$COVER/cover${i + 1}/500/500",
            audioUrl = audioPool[i % audioPool.size],
            durationMs = (150_000 + (i * 6_143) % 130_000).toLong(),
            album = "${titleNouns[i % titleNouns.size]} EP",
        )
    }

    private fun slice(from: Int, count: Int): List<Song> =
        songs.drop(from).take(count)

    val globalPlaylists: List<Playlist> = listOf(
        playlist("pl_g1", "Today's Top Hits", "global1", PlaylistCategory.WORLD, slice(0, 12)),
        playlist("pl_g2", "Chill Vibes", "global2", PlaylistCategory.WORLD, slice(10, 12)),
        playlist("pl_g3", "Focus Flow", "global3", PlaylistCategory.WORLD, slice(20, 10)),
        playlist("pl_g4", "Night Drive", "global4", PlaylistCategory.WORLD, slice(28, 12)),
        playlist("pl_g5", "Fresh Finds", "global5", PlaylistCategory.WORLD, slice(38, 10)),
        playlist("pl_g6", "Workout Energy", "global6", PlaylistCategory.WORLD, slice(44, 10)),
    )

    val localPlaylists: List<Playlist> = listOf(
        playlist("pl_l1", "Persian Pop", "local1", PlaylistCategory.LOCAL, slice(2, 10)),
        playlist("pl_l2", "Local Legends", "local2", PlaylistCategory.LOCAL, slice(14, 10)),
        playlist("pl_l3", "Homegrown", "local3", PlaylistCategory.LOCAL, slice(24, 10)),
        playlist("pl_l4", "Café Acoustic", "local4", PlaylistCategory.LOCAL, slice(34, 8)),
    )

    val userPlaylists: List<Playlist> = listOf(
        playlist("pl_u1", "My Favorites", "me", PlaylistCategory.USER, slice(0, 8)),
        playlist("pl_u2", "Road Trip", "me", PlaylistCategory.USER, slice(16, 9)),
        playlist("pl_u3", "Late Night", "me", PlaylistCategory.USER, slice(30, 7)),
    )

    val allPlaylists: List<Playlist> = globalPlaylists + localPlaylists + userPlaylists

    /** The signed-in user (Premium flag comes from DataStore at runtime). */
    val currentUser = User(
        id = "me",
        name = "Hamed",
        username = "@hamed",
        avatarUrl = "$COVER/me/300/300",
        isPremium = false,
        followerCount = 128,
        followingCount = 87,
    )

    /** Other users for the social / chat features. */
    val otherUsers: List<User> = listOf(
        User("u1", "Sara", "@sara_m", "$COVER/u1/300/300", followerCount = 340),
        User("u2", "Reza", "@reza.k", "$COVER/u2/300/300", followerCount = 512),
        User("u3", "Nazanin", "@naznz", "$COVER/u3/300/300", followerCount = 210),
        User("u4", "Amir", "@amir99", "$COVER/u4/300/300", followerCount = 98),
        User("u5", "Mina", "@minaa", "$COVER/u5/300/300", followerCount = 764),
    )

    val conversationsSeed: List<Conversation> = otherUsers.take(3).mapIndexed { i, u ->
        Conversation(
            id = "conv_${u.id}",
            participant = u,
            lastMessage = null,
            unreadCount = if (i == 0) 2 else 0,
        )
    }

    fun songById(id: String): Song? = songs.firstOrNull { it.id == id }
    fun artistById(id: String): Artist? = artists.firstOrNull { it.id == id }
    fun playlistById(id: String): Playlist? = allPlaylists.firstOrNull { it.id == id }
    fun userById(id: String): User? =
        (listOf(currentUser) + otherUsers).firstOrNull { it.id == id }

    private fun playlist(
        id: String,
        title: String,
        seed: String,
        category: PlaylistCategory,
        songs: List<Song>,
    ) = Playlist(
        id = id,
        title = title,
        coverImageUrl = "$COVER/$seed/500/500",
        category = category,
        ownerId = if (category == PlaylistCategory.USER) "me" else null,
        songCount = songs.size,
        songs = songs,
    )
}
