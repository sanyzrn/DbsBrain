package ir.dbsgraphic.secondbrain.core.database

import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Setup for the FTS5 search table and its sync triggers.
 *
 * Centralized here because the table must be created on BOTH paths:
 *  - upgrades (v2→v3) via [MIGRATION_2_3], and
 *  - fresh installs, where Room creates only the @Entity tables and never runs
 *    migrations — so a DB callback must create the virtual table too.
 *
 * Everything is `IF NOT EXISTS` and the backfill is idempotent, so running it on
 * every open is safe and also self-heals databases created before this fix.
 */
object FtsSchema {

    fun createTableAndTriggers(db: SupportSQLiteDatabase) {
        db.execSQL(
            "CREATE VIRTUAL TABLE IF NOT EXISTS `items_fts` USING fts5(" +
                "itemId UNINDEXED, norm, " +
                "tokenize = \"unicode61 remove_diacritics 2\")",
        )
        val normNew = PersianNormalizer.sqlExpression("new.content")
        db.execSQL(
            "CREATE TRIGGER IF NOT EXISTS items_fts_ai AFTER INSERT ON items BEGIN " +
                "INSERT INTO items_fts(itemId, norm) VALUES (new.id, $normNew); END",
        )
        db.execSQL(
            "CREATE TRIGGER IF NOT EXISTS items_fts_ad AFTER DELETE ON items BEGIN " +
                "DELETE FROM items_fts WHERE itemId = old.id; END",
        )
        db.execSQL(
            "CREATE TRIGGER IF NOT EXISTS items_fts_au AFTER UPDATE ON items BEGIN " +
                "UPDATE items_fts SET norm = $normNew WHERE itemId = new.id; END",
        )
    }

    /** Insert any items missing from the FTS index (idempotent; repairs drift). */
    fun backfillMissing(db: SupportSQLiteDatabase) {
        val normContent = PersianNormalizer.sqlExpression("content")
        db.execSQL(
            "INSERT INTO items_fts(itemId, norm) " +
                "SELECT id, $normContent FROM items WHERE id NOT IN (SELECT itemId FROM items_fts)",
        )
    }
}
