/*
 * Decompiled with CFR 0.152.
 */
package com.coaldust.ideology;

public enum IdeologyStructure {
    CHURCH("church"),
    DOCTRINE("doctrine"),
    CULT("cult"),
    ANIMISM("animism");

    public final String id;

    private IdeologyStructure(String string2) {
        this.id = string2;
    }

    public static IdeologyStructure byId(String string) {
        for (IdeologyStructure ideologyStructure : IdeologyStructure.values()) {
            if (!ideologyStructure.id.equals(string)) continue;
            return ideologyStructure;
        }
        return null;
    }
}
