# SQLCipher native bindings must survive shrinking.
-keep class net.zetetic.database.** { *; }
-keep class net.sqlcipher.** { *; }

# Room generated code is safe by default; keep entities' no-arg paths.
-keep class ir.dbsgraphic.secondbrain.core.database.** { *; }
