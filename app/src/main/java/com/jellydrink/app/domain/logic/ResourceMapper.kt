package com.jellydrink.app.domain.logic

import androidx.annotation.StringRes
import com.jellydrink.app.R
import com.jellydrink.app.domain.logic.GameConstants.CAT_GIORNI
import com.jellydrink.app.domain.logic.GameConstants.CAT_LITRI
import com.jellydrink.app.domain.logic.GameConstants.CAT_LIVELLI
import com.jellydrink.app.domain.logic.GameConstants.CAT_PRIMI_PASSI
import com.jellydrink.app.domain.logic.GameConstants.CAT_SFIDE
import com.jellydrink.app.domain.logic.GameConstants.CAT_STREAK

/**
 * Mappa i tipi/ID alle risorse stringa Android.
 * Questo file restera' nel modulo Android quando si migra a KMP,
 * perche' dipende da R.string.
 */
object ResourceMapper {

    @StringRes
    fun badgeNameRes(type: String): Int = when (type) {
        "first_sip"        -> R.string.badge_name_first_sip
        "daily_goal"       -> R.string.badge_name_daily_goal
        "streak_3"         -> R.string.badge_name_streak_3
        "streak_7"         -> R.string.badge_name_streak_7
        "streak_14"        -> R.string.badge_name_streak_14
        "streak_30"        -> R.string.badge_name_streak_30
        "streak_100"       -> R.string.badge_name_streak_100
        "liters_10"        -> R.string.badge_name_liters_10
        "liters_50"        -> R.string.badge_name_liters_50
        "liters_100"       -> R.string.badge_name_liters_100
        "liters_500"       -> R.string.badge_name_liters_500
        "liters_1000"      -> R.string.badge_name_liters_1000
        "active_7"         -> R.string.badge_name_active_7
        "active_30"        -> R.string.badge_name_active_30
        "active_100"       -> R.string.badge_name_active_100
        "active_365"       -> R.string.badge_name_active_365
        "level_5"          -> R.string.badge_name_level_5
        "level_10"         -> R.string.badge_name_level_10
        "level_20"         -> R.string.badge_name_level_20
        "level_50"         -> R.string.badge_name_level_50
        "challenges_10"    -> R.string.badge_name_challenges_10
        "challenges_50"    -> R.string.badge_name_challenges_50
        "challenges_100"   -> R.string.badge_name_challenges_100
        "challenges_150"   -> R.string.badge_name_challenges_150
        "challenges_200"   -> R.string.badge_name_challenges_200
        else               -> 0
    }

    @StringRes
    fun badgeDescRes(type: String): Int = when (type) {
        "first_sip"        -> R.string.badge_desc_first_sip
        "daily_goal"       -> R.string.badge_desc_daily_goal
        "streak_3"         -> R.string.badge_desc_streak_3
        "streak_7"         -> R.string.badge_desc_streak_7
        "streak_14"        -> R.string.badge_desc_streak_14
        "streak_30"        -> R.string.badge_desc_streak_30
        "streak_100"       -> R.string.badge_desc_streak_100
        "liters_10"        -> R.string.badge_desc_liters_10
        "liters_50"        -> R.string.badge_desc_liters_50
        "liters_100"       -> R.string.badge_desc_liters_100
        "liters_500"       -> R.string.badge_desc_liters_500
        "liters_1000"      -> R.string.badge_desc_liters_1000
        "active_7"         -> R.string.badge_desc_active_7
        "active_30"        -> R.string.badge_desc_active_30
        "active_100"       -> R.string.badge_desc_active_100
        "active_365"       -> R.string.badge_desc_active_365
        "level_5"          -> R.string.badge_desc_level_5
        "level_10"         -> R.string.badge_desc_level_10
        "level_20"         -> R.string.badge_desc_level_20
        "level_50"         -> R.string.badge_desc_level_50
        "challenges_10"    -> R.string.badge_desc_challenges_10
        "challenges_50"    -> R.string.badge_desc_challenges_50
        "challenges_100"   -> R.string.badge_desc_challenges_100
        "challenges_150"   -> R.string.badge_desc_challenges_150
        "challenges_200"   -> R.string.badge_desc_challenges_200
        else               -> 0
    }

    @StringRes
    fun challengeDescRes(type: String): Int = when (type) {
        "early_bird"      -> R.string.challenge_early_bird
        "consistent"      -> R.string.challenge_consistent
        "big_gulp"        -> R.string.challenge_big_gulp
        "afternoon_goal"  -> R.string.challenge_afternoon_goal
        "full_tank"       -> R.string.challenge_full_tank
        else              -> 0
    }

    @StringRes
    fun decoNameRes(id: String): Int = when (id) {
        "fish_blue"   -> R.string.deco_fish_blue
        "fish_orange" -> R.string.deco_fish_orange
        "starfish"    -> R.string.deco_starfish
        "coral_pink"  -> R.string.deco_coral_pink
        "treasure"    -> R.string.deco_treasure
        "turtle"      -> R.string.deco_turtle
        "seahorse"    -> R.string.deco_seahorse
        "crab"        -> R.string.deco_crab
        else          -> 0
    }

    @StringRes
    fun categoryNameRes(category: String): Int = when (category) {
        CAT_PRIMI_PASSI -> R.string.badge_cat_primi_passi
        CAT_STREAK      -> R.string.badge_cat_streak
        CAT_LITRI       -> R.string.badge_cat_litri
        CAT_GIORNI      -> R.string.badge_cat_giorni
        CAT_LIVELLI     -> R.string.badge_cat_livelli
        CAT_SFIDE       -> R.string.badge_cat_sfide
        else            -> 0
    }
}
