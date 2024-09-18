package com.github.theredbrain.equipmentsets.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.data.TrackedDataHandler;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.encoding.StringEncoding;
import net.minecraft.network.encoding.VarInts;

import java.util.HashMap;
import java.util.Map;

public class CustomDataHandlers {
	private static final PacketCodec<ByteBuf, Map<String, Integer>> STRING_INT_MAP = new PacketCodec<>() {
		public Map<String, Integer> decode(ByteBuf byteBuf) {
			int i = VarInts.read(byteBuf);
			Map<String, Integer> propertyMap = new HashMap<>();

			for (int j = 0; j < i; j++) {
				String string = StringEncoding.decode(byteBuf, Integer.MAX_VALUE);
				int integer = VarInts.read(byteBuf);
				propertyMap.put(string, integer);
			}

			return propertyMap;
		}

		public void encode(ByteBuf byteBuf, Map<String, Integer> stringIntMap) {
			VarInts.write(byteBuf, stringIntMap.size());

			for (var entry : stringIntMap.entrySet()) {
				StringEncoding.encode(byteBuf, entry.getKey(), Integer.MAX_VALUE);
				VarInts.write(byteBuf, entry.getValue());
			}
		}
	};

	public static final TrackedDataHandler<Map<String, Integer>> STRING_INTEGER_MAP;

	static {

		STRING_INTEGER_MAP = new TrackedDataHandler.ImmutableHandler<>() {
			@Override
			public PacketCodec<? super RegistryByteBuf, Map<String, Integer>> codec() {
				return STRING_INT_MAP;
			}

			public Map<String, Integer> copy(Map<String, Integer> map) {
				return new HashMap<>(map);
			}
		};
		TrackedDataHandlerRegistry.register(STRING_INTEGER_MAP);
	}
}
