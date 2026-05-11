package com.lboro.msbr.ui.comparison

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.room.Room
import com.lboro.msbr.CompApp
import com.lboro.msbr.data.database.CompDatabase
import com.lboro.msbr.data.database.CompDatabase.Companion.DB_NAME
import com.lboro.msbr.data.database.MovieDetailsEntry
import com.lboro.msbr.data.database.ProviderEntry
import com.lboro.msbr.data.database.getProviders
import com.lboro.msbr.data.database.parseProviders
import kotlinx.coroutines.launch

class DBViewModel(application: Application): AndroidViewModel(application) {
    private val context: Context
        get() = getApplication()

    private val db = Room.databaseBuilder(context, CompDatabase::class.java, DB_NAME)
        .fallbackToDestructiveMigration(false)
        //.addMigrations() /*for potential future live updates*/
        .build()

    val movieStreamingServices = mapOf(
        "Netflix" to "https://www.netflix.com",
        "Disney+" to "https://www.disneyplus.com",
        "Max" to "https://www.max.com",
        "Hulu" to "https://www.hulu.com",
        "Amazon Prime Video" to "https://www.primevideo.com",
        "Apple TV+" to "https://tv.apple.com",
        "Paramount+" to "https://www.paramountplus.com",
        "Peacock" to "https://www.peacocktv.com",
        "MUBI" to "https://mubi.com",
        "Criterion Channel" to "https://www.criterionchannel.com",
        "Shudder" to "https://www.shudder.com",
        "BFI Player" to "https://player.bfi.org.uk",
        "Rakuten TV" to "https://www.rakuten.tv",
        "NOW" to "https://www.nowtv.com",
        "STARZ" to "https://www.starz.com",
        "AMC+" to "https://www.amcplus.com",
        "Tubi" to "https://tubitv.com",
        "Pluto TV" to "https://pluto.tv",
        "Plex" to "https://www.plex.tv",
        "FilmRise" to "https://filmrise.com",
        "Crunchyroll" to "https://www.crunchyroll.com",
        "HIDIVE" to "https://www.hidive.com",
        "Acorn TV" to "https://acorn.tv",
        "BritBox" to "https://www.britbox.com",
        "Sundance Now" to "https://www.sundancenow.com",
        "Dekkoo" to "https://www.dekkoo.com",
        "Curiosity Stream" to "https://curiositystream.com",
        "Kanopy" to "https://www.kanopy.com",
        "Hoopla" to "https://www.hoopladigital.com",
        "Vudu" to "https://www.vudu.com",
        "Fandango at Home" to "https://www.fandangohome.com",
        "Freevee" to "https://www.amazon.com/freevee",
        "Crackle" to "https://www.crackle.com",
        "Popcornflix" to "https://www.popcornflix.com",
        "Xumo Play" to "https://play.xumo.com",
        "Filmzie" to "https://filmzie.com",
        "Docsville" to "https://docsville.com",
        "DOCSVILLE" to "https://www.docsville.com",
        "True Story" to "https://watchtruestory.com",
        "Topic" to "https://www.topic.com",
        "MHz Choice" to "https://watch.mhzchoice.com",
        "Viaplay" to "https://viaplay.com",
        "ZEE5" to "https://www.zee5.com",
        "Sony LIV" to "https://www.sonyliv.com",
        "aha" to "https://www.aha.video",
        "Eros Now" to "https://erosnow.com",
        "iQIYI" to "https://www.iq.com",
        "Viki" to "https://www.viki.com",
        "Tencent Video" to "https://v.qq.com",
        "Youku" to "https://www.youku.com",
        "Bilibili" to "https://www.bilibili.tv",
        "wavve" to "https://www.wavve.com",
        "Watcha" to "https://watcha.com",
        "Stan" to "https://www.stan.com.au",
        "Foxtel Now" to "https://www.foxtel.com.au/watch/foxtel-now.html",
        "Crave" to "https://www.crave.ca",
        "CBC Gem" to "https://gem.cbc.ca",
        "ICI TOU.TV" to "https://ici.tou.tv",
        "Showmax" to "https://www.showmax.com",
        "iflix" to "https://www.iflix.com",
        "Viu" to "https://www.viu.com",
        "RTL+" to "https://www.rtlplus.com",
        "Joyn" to "https://www.joyn.de",
        "MagentaTV" to "https://www.telekom.de/magenta-tv",
        "Canal+" to "https://www.canalplus.com",
        "Salto" to "https://www.salto.fr",
        "Filmin" to "https://www.filmin.es",
        "Movistar Plus+" to "https://ver.movistarplus.es",
        "SkyShowtime" to "https://www.skyshowtime.com",
        "Discovery+" to "https://www.discoveryplus.com",
        "BET+" to "https://www.bet.plus",
        "ALLBLK" to "https://www.allblk.tv",
        "Revry" to "https://watch.revry.tv",
        "OUTtv" to "https://www.outtvgo.com",
        "Arrow Player" to "https://www.arrow-player.com",
        "Midnight Pulp" to "https://www.midnightpulp.com",
        "AsianCrush" to "https://www.asiancrush.com",
        "RetroCrush" to "https://www.retrocrush.tv",
        "Cineverse" to "https://www.cineverse.com",
        "Classix" to "https://www.classixapp.com",
        "YuppTV" to "https://www.yupptv.com",
        "MX Player" to "https://www.mxplayer.in",
        "JioCinema" to "https://www.jiocinema.com",
        "Hotstar" to "https://www.hotstar.com",
        "Tencent WeTV" to "https://wetv.vip",
        "PuhuTV" to "https://puhutv.com",
        "RTL Play" to "https://www.rtlplay.be",
        "NRK TV" to "https://tv.nrk.no",
        "SVT Play" to "https://www.svtplay.se",
        "DR TV" to "https://www.dr.dk/drtv",
        "TV 2 Play" to "https://play.tv2.no",
        "RaiPlay" to "https://www.raiplay.it",
        "Mediaset Infinity" to "https://mediasetinfinity.mediaset.it",
        "Arte.tv" to "https://www.arte.tv",
        "CDA Premium" to "https://www.cda.pl",
        "Player.pl" to "https://player.pl",
        "Kinopoisk" to "https://www.kinopoisk.ru",
        "ivi" to "https://www.ivi.ru",
        "Okko" to "https://okko.tv",
        "Megogo" to "https://megogo.net",
        "OSN+" to "https://stream.osnplus.com",
        "Shahid" to "https://shahid.mbc.net",
        "StarzPlay" to "https://starzplay.com",
        "MGM+" to "https://www.mgmplus.com",
        "The Roku Channel" to "https://therokuchannel.roku.com",
        "Piksel" to "https://www.piksel.com",
        "Eventive" to "https://watch.eventive.org",
        "NFB" to "https://www.nfb.ca",
        "DocPlay" to "https://www.docplay.com",
        "GuideDoc" to "https://www.guidedoc.tv",
        "OVID.tv" to "https://www.ovid.tv",
        "Fawesome" to "https://fawesome.tv",
        "Dark Matter TV" to "https://darkmattertv.com",
        "Screambox" to "https://www.screambox.com",
        "Cultpix" to "https://www.cultpix.com",
        "Spamflix" to "https://www.spamflix.com",
        "Classic Cinema Online" to "http://classiccinemaonline.com",
        "SnagFilms" to "https://www.snagfilms.com",
        "DistroTV" to "https://www.distro.tv",
        "Poki Cinema" to "https://poki.com",
        "Netzkino" to "https://www.netzkino.de",
        "TVING" to "https://www.tving.com",
        "U-NEXT" to "https://video.unext.jp",
        "ABEMA" to "https://abema.tv",
        "Lionsgate Play" to "https://www.lionsgateplay.com",
        "Chili" to "https://www.chili.com",
        "Videoland" to "https://www.videoland.com",
        "Pathé Thuis" to "https://www.pathe-thuis.nl",
        "Rakuten Viki" to "https://www.viki.com",
        "Crunchyroll Store" to "https://store.crunchyroll.com",
        "LaCinetek" to "https://www.lacinetek.com",
        "UniversCiné" to "https://www.universcine.com",
        "Cinemember" to "https://www.cinemember.nl",
        "Filmo" to "https://www.filmo.fr",
        "Acontra+" to "https://acontra.plus",
        "Takflix" to "https://takflix.com",
        "Einthusan" to "https://einthusan.tv",
        "MovieSaints" to "https://moviesaints.com",
        "Ultraflix" to "https://www.ultraflix.com",
        "VHX" to "https://www.vhx.tv",
        "Mometu" to "https://mometu.com",
        "Fearless" to "https://watchfearless.com",
        "Klassiki" to "https://klassiki.online",
        "FilmDoo" to "https://www.filmdoo.com",
        "Cineasterna" to "https://www.cineasterna.com",
        "Projectr" to "https://projectr.tv",
        "myfilmfriend" to "https://myfilmfriend.com",
        "DocAlliance Films" to "https://dafilms.com"
    )




    /****************************************************
     PROVIDERS
     ****************************************************/
    fun getAllProviders(){
        viewModelScope.launch {
            Log.i("records",db.providerDao().getAll().toString())
        }
    }

    suspend fun getLink(name: String): String? {
        var out = db.providerDao().getLink(name)
        Log.i("url", out?: "none")
        return out
    }

    suspend fun updateLinks() {
        for ((key, value) in movieStreamingServices) {
            try {
                db.providerDao().updateLinks(key, value)
            } catch (e: Exception) {
                Log.e("Error", e.toString())
            }
        }

    }

    suspend fun seedProviders() {
        parseProviders(getProviders()).forEach { provider ->
            db.providerDao().insert(provider)
        }
    }

    /****************************************************
     CACHE
     ****************************************************/
    suspend fun cacheMovie(movieDetailsEntry: MovieDetailsEntry) {
        db.movieDetailsDao().insert(movieDetailsEntry)
    }
    suspend fun getMovieCache(): List<MovieDetailsEntry> {
        return db.movieDetailsDao().getAll()
    }

    suspend fun getMovieNames(): List<String> {
        return db.movieDetailsDao().getMovieNames()
    }

    suspend fun getMovie(movie: String): Array<MovieDetailsEntry>? {
        return db.movieDetailsDao().getMovie(movie)
    }

    suspend fun clearCache() {
        db.movieDetailsDao().clearCache()
    }

    suspend fun clearCacheOf(movie: String) {
        db.movieDetailsDao().delete(movie)
    }
}

class DBViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val app =
            extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as CompApp
        return DBViewModel(app) as T
    }
}