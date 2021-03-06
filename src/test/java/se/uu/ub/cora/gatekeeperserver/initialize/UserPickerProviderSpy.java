/*
 * Copyright 2016, 2019 Uppsala University Library
 *
 * This file is part of Cora.
 *
 *     Cora is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Cora is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with Cora.  If not, see <http://www.gnu.org/licenses/>.
 */

package se.uu.ub.cora.gatekeeperserver.initialize;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.uu.ub.cora.gatekeeper.user.UserPicker;
import se.uu.ub.cora.gatekeeper.user.UserPickerProvider;
import se.uu.ub.cora.gatekeeper.user.UserStorage;
import se.uu.ub.cora.gatekeeperserver.UserPickerSpy;

public class UserPickerProviderSpy implements UserPickerProvider {
	public List<UserPickerSpy> factoredUserPickers = new ArrayList<>();
	private Map<String, String> initInfo;
	private UserStorage userStorage;
	private String guestUserId;

	public UserPickerProviderSpy(Map<String, String> initInfo) {
		this.initInfo = initInfo;
	}

	@Override
	public UserPicker getUserPicker() {
		UserPickerSpy userPickerSpy = new UserPickerSpy();
		factoredUserPickers.add(userPickerSpy);
		return userPickerSpy;
	}

	public Map<String, String> getInitInfo() {
		return initInfo;
	}

	@Override
	public void startUsingUserStorageAndGuestUserId(UserStorage userStorage, String guestUserId) {
		this.userStorage = userStorage;
		this.guestUserId = guestUserId;
	}

	public String guestUserId() {
		return guestUserId;
	}

	public UserStorage getUserStorage() {
		return userStorage;
	}

	@Override
	public int getOrderToSelectImplementionsBy() {
		return 0;
	}

}
