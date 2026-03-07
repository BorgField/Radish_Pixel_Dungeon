/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndClericSpells;
import com.watabou.noosa.Visual;

public class ClericSkills extends Buff implements ActionIndicator.Action {

	{
		type = buffType.POSITIVE;
		announced = false;
	}

	@Override
	public int icon() {
		return BuffIndicator.NONE;
	}

	@Override
	public boolean act() {
		spend(TICK);
		return true;
	}

	@Override
	public String actionName() {
		return Messages.get(this, "name");
	}

	@Override
	public int actionIcon() {
		return HeroIcon.CLERIC_SPELLS;
	}

	@Override
	public Visual secondaryVisual() {
		return null;
	}

	@Override
	public int indicatorColor() {
		return 0xFF4444; // 红色，象征牧师的神圣力量
	}

	@Override
	public void doAction() {
		GameScene.show(new WndClericSpells((Hero)target));
	}

	// 静态方法，用于为牧师添加技能buff
	public static void init(Hero hero) {
		ClericSkills skills = hero.buff(ClericSkills.class);
		if (skills == null) {
			Buff.affect(hero, ClericSkills.class);
		}
		ActionIndicator.setAction(hero.buff(ClericSkills.class));
	}
}
