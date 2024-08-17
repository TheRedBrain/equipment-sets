package com.github.theredbrain.equipmentsets.entity;

import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.PacketByteBuf;

import java.util.Map;

public class CustomDataHandlers {

	public static final TrackedDataHandler<Map<String, Integer>> STRING_INTEGER_MAP = new TrackedDataHandler.ImmutableHandler<Map<String, Integer>>() {
		public void write(PacketByteBuf packetByteBuf, Map<String, Integer> map) {
			packetByteBuf.writeMap(map, new PacketByteBuf.PacketWriter<String>() {
				@Override
				public void accept(PacketByteBuf packetByteBuf, String string) {
					packetByteBuf.writeString(string);
				}
			}, new PacketByteBuf.PacketWriter<Integer>() {
				@Override
				public void accept(PacketByteBuf packetByteBuf, Integer integer) {
					packetByteBuf.writeInt(integer);
				}
			});
		}

		public Map<String, Integer> read(PacketByteBuf packetByteBuf) {
			return packetByteBuf.readMap(
					new PacketByteBuf.PacketReader<String>() {
						@Override
						public String apply(PacketByteBuf packetByteBuf) {
							return packetByteBuf.readString();
						}
					}, new PacketByteBuf.PacketReader<Integer>() {
						@Override
						public Integer apply(PacketByteBuf packetByteBuf) {
							return packetByteBuf.readInt();
						}
					}
			);
		}
	};

	static {
		TrackedDataHandlerRegistry.register(STRING_INTEGER_MAP);
	}
}
