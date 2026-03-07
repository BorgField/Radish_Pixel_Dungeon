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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.skills.ClericSpell;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.ActionIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.watabou.noosa.ColorBlock;
import com.watabou.noosa.NinePatch;

import java.util.ArrayList;

public class WndClericSpells extends Window {

	protected static final int WIDTH_P = 120;
	protected static final int WIDTH_L = 160;
	protected static final int MARGIN = 2;
	public static int BTN_SIZE = 20;

	public WndClericSpells(Hero cleric) {
		super();

		int width = PixelScene.landscape() ? WIDTH_L : WIDTH_P;

		// 标题
		RenderedTextBlock title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(this, "title")), 9);
		title.hardlight(TITLE_COLOR);
		title.setPos((width - title.width()) / 2, MARGIN);
		title.maxWidth(width - MARGIN * 2);
		add(title);

		float pos = title.bottom() + 3 * MARGIN;

		// 按天赋层级显示技能
		for (int i = 1; i <= Talent.MAX_TALENT_TIERS; i++) {
			ArrayList<ClericSpell> spells = ClericSpell.getSpellList(cleric, i);

			if (!spells.isEmpty()) {
				if (i != 1) {
					// 添加分隔线
					pos += MARGIN;
					ColorBlock sep = new ColorBlock(width, 1, 0xFF000000);
					sep.y = pos;
					add(sep);
					pos += 3 * MARGIN;
				}

				// 层级标题
				RenderedTextBlock tierTitle = PixelScene.renderTextBlock(Messages.get(this, "tier", i), 7);
				tierTitle.hardlight(0xCCCCCC);
				tierTitle.setPos(MARGIN, pos);
				tierTitle.maxWidth(width - MARGIN * 2);
				add(tierTitle);
				pos = tierTitle.bottom() + MARGIN;

				// 添加技能按钮
				ArrayList<IconButton> spellBtns = new ArrayList<>();
				for (ClericSpell spell : spells) {
					IconButton spellBtn = new SpellButton(spell);
					add(spellBtn);
					spellBtns.add(spellBtn);
				}

				// 居中排列按钮
				int left = MARGIN + (width - spellBtns.size() * (BTN_SIZE + MARGIN)) / 2;
				for (IconButton btn : spellBtns) {
					btn.setRect(left, pos, BTN_SIZE, BTN_SIZE);
					left += BTN_SIZE + MARGIN;
				}

				pos += BTN_SIZE + MARGIN;
			}
		}

		resize(width, (int)pos);

		// 在移动设备上调整窗口位置
		if (SPDSettings.interfaceSize() != 2) {
			offset(0, (int) (GameScene.uiCamera.height / 2 - 30 - height / 2));
		}
	}

	protected class SpellButton extends IconButton {

		private ClericSpell spell;
		private NinePatch bg;

		public SpellButton(ClericSpell spell){
			super(new HeroIcon((ActionIndicator.Action) spell));

			this.spell = spell;

			if (!spell.canCast(Dungeon.hero)){
				icon.alpha(0.3f);
			}

			bg = Chrome.get(Chrome.Type.TOAST);
			addToBack(bg);
		}

		@Override
		protected void layout() {
			super.layout();

			if (bg != null) {
				bg.size(width, height);
				bg.x = x;
				bg.y = y;
			}
		}

		@Override
		protected void onClick() {
			if (spell.canCast(Dungeon.hero)) {
				spell.onCast(Dungeon.hero);
				hide();
			} else {
				GLog.w(Messages.get(this, "cannot_cast"));
			}
		}

		@Override
		protected String hoverText() {
			return "_" + Messages.titleCase(spell.name()) + "_\n" + spell.shortDesc();
		}
	}
}
