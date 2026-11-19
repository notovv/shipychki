package ru.shipuchki;

import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.ZombieEntityRenderer;
import net.minecraft.client.render.entity.state.ZombieEntityRenderState;
import net.minecraft.util.Identifier;

public class NpcRenderer extends ZombieEntityRenderer {
	private final Identifier texture;

	public NpcRenderer(EntityRendererFactory.Context ctx, String name) {
		super(ctx);
		this.texture = Identifier.of(ShipuchkiMod.MOD_ID, "textures/entity/" + name + ".png");
	}

	@Override
	public Identifier getTexture(ZombieEntityRenderState state) {
		return texture;
	}
}
