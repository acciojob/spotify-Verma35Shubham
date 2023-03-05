package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User user = new User(name, mobile);
        users.add(user);
        return  user;
    }

    public Artist createArtist(String name) {
        Artist artist = new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        Artist artist1 = null;

        for(Artist artist:artists){
            if(artist.getName().equals(artistName)){
                artist1=artist;
                break;
            }
        }
        if(artist1==null){
            artist1 = createArtist(artistName);

            Album album = new Album();

            album.setTitle(title);
            album.setReleaseDate(new Date());

            albums.add(album);

            List<Album> l = new ArrayList<>();
            l.add(album);
            artistAlbumMap.put(artist1,l);

            return album;
        }else {
            Album album = new Album();

            album.setTitle(title);
            album.setReleaseDate(new Date());

            albums.add(album);

            List<Album> l = artistAlbumMap.get(artist1);
            if(l == null){
                l = new ArrayList<>();
            }
            l.add(album);
            artistAlbumMap.put(artist1,l);

            return album;
        }
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        Song song = new Song(title, length);
        Album albumFromList = null;
        for (Album album : albums){
            if(album.getTitle() == albumName){
                albumFromList = album;
                break;
            }
        }
        if(albumFromList == null){
            throw new Exception("Album does not exist");
        }
        else{
            songs.add(song);
            if(albumSongMap.containsKey(albumFromList)){
                List<Song> list = albumSongMap.get(albumFromList);
                list.add(song);
                albumSongMap.put(albumFromList, list);
            }
            else{
                List<Song> list = new ArrayList<>();
                list.add(song);
                albumSongMap.put(albumFromList, list);
            }
        }

        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        Playlist playlist = new Playlist(title);
        User user = null;
        for(User u : users){
            if(u.getMobile().equals(mobile)){
                user = u;
                break;
            }
        }

        if(user == null){
            throw new Exception("User does not exist");
        }
        else{
            playlists.add(playlist);
            List<Song> Songlist = new ArrayList<>();
            for(Song song : songs){
                if(song.getLength() == length){
                    Songlist.add(song);
                }
            }
            playlistSongMap.put(playlist, Songlist);

            List<User> userList = new ArrayList<>();
            userList.add(user);
            playlistListenerMap.put(playlist, userList);
            creatorPlaylistMap.put(user, playlist);
            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user, userPlayList);
            }
            else{
                List<Playlist> playlists1 = new ArrayList<>();
                playlists1.add(playlist);
                userPlaylistMap.put(user, playlists1);
            }
        }
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user==null)
            throw new Exception("User does not exist");
        else {
            Playlist playlist = new Playlist();
            playlist.setTitle(title);
            playlists.add(playlist);

            List<Song> l = new ArrayList<>();
            for(Song song:songs){
                if(songTitles.contains(song.getTitle())){
                    l.add(song);
                }
            }
            playlistSongMap.put(playlist,l);

            List<User> list = new ArrayList<>();
            list.add(user);
            playlistListenerMap.put(playlist,list);

            creatorPlaylistMap.put(user,playlist);

            if(userPlaylistMap.containsKey(user)){
                List<Playlist> userPlayList = userPlaylistMap.get(user);
                userPlayList.add(playlist);
                userPlaylistMap.put(user,userPlayList);
            }else{
                List<Playlist> plays = new ArrayList<>();
                plays.add(playlist);
                userPlaylistMap.put(user,plays);
            }

            return playlist;
        }
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        Playlist playlist = null;
        User user = null;
        for(User u : users){
            if(u.getMobile().equals(mobile)){
                user = u;
                break;
            }
        }

        if(user == null){
            throw new Exception("User does not exist");
        }
        else{
            for(Playlist playlist1 : playlists){
                if (playlist1.getTitle().equals(playlistTitle)){
                    playlist = playlist1;
                    break;
                }
            }
            if(playlist == null){
                throw new Exception("Playlist does not exist");
            }
            if(creatorPlaylistMap.containsKey(user)){
                return playlist;
            }
            List<User> listner = playlistListenerMap.get(playlist);
            for (User user1 : listner){
                if (user1 == user){
                    return playlist;
                }
            }
            listner.add(user);
            playlistListenerMap.put(playlist, listner);

            List<Playlist> playlistList = userPlaylistMap.get(user);
            if(playlistList == null){
                playlistList = new ArrayList<>();
            }
            playlistList.add(playlist);
            userPlaylistMap.put(user, playlistList);
        }
        return playlist;
    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        User user = null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user==null) {
            throw new Exception("User does not exist");
        }

        Song song = null;
        for(Song song1:songs){
            if(song1.getTitle().equals(songTitle)){
                song=song1;
                break;
            }
        }
        if (song==null) {
            throw new Exception("Song does not exist");
        }

        if(songLikeMap.containsKey(song)){
            List<User> list = songLikeMap.get(song);
            if(list.contains(user)){
                return song;
            }else {
                int likes = song.getLikes() + 1;
                song.setLikes(likes);
                list.add(user);
                songLikeMap.put(song,list);

                Album album=null;
                for(Album album1:albumSongMap.keySet()){
                    List<Song> songList = albumSongMap.get(album1);
                    if(songList.contains(song)){
                        album = album1;
                        break;
                    }
                }
                Artist artist = null;
                for(Artist artist1:artistAlbumMap.keySet()){
                    List<Album> albumList = artistAlbumMap.get(artist1);
                    if (albumList.contains(album)){
                        artist = artist1;
                        break;
                    }
                }
                int likes1 = artist.getLikes() +1;
                artist.setLikes(likes1);
                artists.add(artist);
                return song;
            }
        }else {
            int likes = song.getLikes() + 1;
            song.setLikes(likes);
            List<User> list = new ArrayList<>();
            list.add(user);
            songLikeMap.put(song, list);

            Album album = null;
            for (Album album1 : albumSongMap.keySet()) {
                List<Song> songList = albumSongMap.get(album1);
                if (songList.contains(song)) {
                    album = album1;
                    break;
                }
            }
            Artist artist = null;
            for (Artist artist1 : artistAlbumMap.keySet()) {
                List<Album> albumList = artistAlbumMap.get(artist1);
                if (albumList.contains(album)) {
                    artist = artist1;
                    break;
                }
            }
            int likes1 = artist.getLikes() + 1;
            artist.setLikes(likes1);
            artists.add(artist);

            return song;
        }
    }

    public String mostPopularArtist() {
        int max_like = 0;
        Artist artist = null;
        for(Artist a : artists){
            if(a.getLikes() > max_like){
                artist = a;
                max_like = a.getLikes();
            }
        }
        return artist == null ? null : artist.getName();
    }

    public String mostPopularSong() {
        int max = 0;
        Song song = null;
        for (Song s : songLikeMap.keySet()){
            if(s.getLikes() > max){
                song = s;
                max = s.getLikes();
            }
        }
        return song == null ? null : song.getTitle();
    }
}