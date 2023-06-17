-dontobfuscate
-allowaccessmodification
-optimizations !code/simplification/arithmetic

-keepattributes SourceFile, LineNumberTable, Exception, *Annotation*, InnerClasses, EnclosingMethod, Signature

-keep class ** implements de.robv.android.xposed.IXposedHookLoadPackage

-assumenosideeffects class kotlin.jvm.internal.Intrinsics { void checkNotNullParameter(java.lang.Object,java.lang.String); }
-assumenosideeffects class kotlin.jvm.internal.Intrinsics { void checkNotNullExpressionValue(java.lang.Object,java.lang.String); }
-assumenosideeffects class kotlin.jvm.internal.Intrinsics { java.lang.Throwable sanitizeStackTrace(java.lang.Throwable, java.lang.String); }

-assumenosideeffects class kotlin.collections.builders.ListBuilder { void checkIsMutable(); }
