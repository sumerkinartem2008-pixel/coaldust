/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.network.codec.ByteBufCodecs
 *  net.minecraft.network.codec.StreamCodec
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload
 *  net.minecraft.network.protocol.common.custom.CustomPacketPayload$Type
 *  net.neoforged.neoforge.network.handling.IPayloadContext
 */
package com.coaldust.meteor;

import com.coaldust.CoalDust;
import com.coaldust.meteor.MeteorRemoteItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record RemoteSizePayload(int hand, int size) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<RemoteSizePayload> TYPE = new CustomPacketPayload.Type(CoalDust.id("remote_size"));
    public static final StreamCodec<ByteBuf, RemoteSizePayload> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, RemoteSizePayload::hand, (StreamCodec)ByteBufCodecs.VAR_INT, RemoteSizePayload::size, RemoteSizePayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(RemoteSizePayload remoteSizePayload, IPayloadContext iPayloadContext) {
        iPayloadContext.enqueueWork(() -> MeteorRemoteItem.applySelection(iPayloadContext.player(), remoteSizePayload.hand(), remoteSizePayload.size()));
    }
}
