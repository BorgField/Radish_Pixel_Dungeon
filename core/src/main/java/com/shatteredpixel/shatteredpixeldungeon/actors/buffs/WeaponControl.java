
package com.shatteredpixel.shatteredpixeldungeon.actors.buffs;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.MeleeWeapon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.watabou.noosa.Image;
import com.watabou.utils.Bundle;

public class WeaponControl extends Buff {

	private String Weapon;
	private int count = 0;
	private int damage = 0;
	private int max = 0;
	public int Weapon_NO = 0;
	private boolean hasBonus = false;

	{
		type = buffType.POSITIVE;
	}

	@Override
	public boolean act() {
		if (Dungeon.hero != null) {
			spend(TICK);
			max = 151 - 25 * Dungeon.hero.pointsInTalent(Talent.MOONLIGHT_T1_3);
		}
		count++;
		if (count >= max &&  damage < 5) {
			damage++;
			hasBonus = true;
			count = 0;
		}
		spend(TICK);
		return true;
	}

	public boolean hasMoonlightBonus() {
		return hasBonus;
	}

	public int getDamage() {
		return damage;
	}
	public void setWeapon(MeleeWeapon weapon) {
		Weapon = weapon.trueName();
	}

	public String getWeapon() {
		return Weapon;
	}

	@Override
	public int icon() {
		return BuffIndicator.WEAPON;
	}

	@Override
	public void tintIcon(Image icon) {
		if (hasBonus) {
			icon.hardlight(0.7f, 0.8f, 1.0f);  // 有加成时显示亮蓝色
		} else {
			icon.hardlight(0.4f, 0.5f, 0.6f);  // 无加成时显示暗蓝色
		}
	}

	@Override
	public String toString() {
		return Messages.get(this, "name");
	}

	@Override
	public String desc() {
		int Change = Math.max(0, max - count);
		return damage == 5 ?
				Messages.get(this, "max_desc", damage) :
				Messages.get(this, "desc", Change, damage);

	}

	private static final String COUNT = "count";
	private static final String DAMAGE = "damage";
	private static final String HAS_BONUS = "has_bonus";
	private static final String WEAPON_ID = "weapon_id";
	private static final String WEAPON_NO = "weapon_no";

	@Override
	public void storeInBundle(Bundle bundle) {
		super.storeInBundle(bundle);
		bundle.put(COUNT, count);
		bundle.put(DAMAGE, damage);
		bundle.put(HAS_BONUS, hasBonus);
		bundle.put(WEAPON_ID, Weapon);
		bundle.put(WEAPON_NO, Weapon_NO);
	}

	@Override
	public void restoreFromBundle(Bundle bundle) {
		super.restoreFromBundle(bundle);
		count = bundle.getInt(COUNT);
		damage = bundle.getInt(DAMAGE);
		hasBonus = bundle.getBoolean(HAS_BONUS);
		Weapon = bundle.getString(WEAPON_ID);
		Weapon_NO = bundle.getInt(WEAPON_NO);
	}

}
