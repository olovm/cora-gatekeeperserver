/*
 * Copyright 2019 Olov McKie
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

import java.util.Map;

import se.uu.ub.cora.gatekeeper.user.UserPickerProvider;
import se.uu.ub.cora.gatekeeper.user.UserStorage;
import se.uu.ub.cora.gatekeeper.user.UserStorageProvider;
import se.uu.ub.cora.gatekeeperserver.GatekeeperImp;
import se.uu.ub.cora.gatekeeperserver.dependency.GatekeeperInstanceProvider;
import se.uu.ub.cora.gatekeeperserver.dependency.GatekeeperLocator;
import se.uu.ub.cora.gatekeeperserver.dependency.GatekeeperLocatorImp;

public class GatekeeperModuleStarterImp implements GatekeeperModuleStarter {
	private Map<String, String> initInfo;
	private Iterable<UserPickerProvider> userPickerProviders;
	private Iterable<UserStorageProvider> userStorageProviders;

	@Override
	public void startUsingInitInfoAndUserPickerProvidersAndUserStorageProviders(
			Map<String, String> initInfo,
			Iterable<UserPickerProvider> userPickerProviderImplementations,
			Iterable<UserStorageProvider> userStorageProviderImplementations) {
		this.initInfo = initInfo;
		this.userPickerProviders = userPickerProviderImplementations;
		this.userStorageProviders = userStorageProviderImplementations;
		start();
	}

	public void start() {
		UserPickerProvider userPickerProvider = getImplementationThrowErrorIfNoneOrMoreThanOne(
				userPickerProviders, "UserPickerProvider");
		String guestUserId = tryToGetInitParameter("guestUserId");

		UserStorageProvider userStorageProvider = getImplementationThrowErrorIfNoneOrMoreThanOne(
				userStorageProviders, "UserStorageProvider");

		userStorageProvider.startUsingInitInfo(initInfo);
		UserStorage userStorage = userStorageProvider.getUserStorage();

		userPickerProvider.startUsingUserStorageAndGuestUserId(userStorage, guestUserId);

		GatekeeperLocator locator = new GatekeeperLocatorImp();
		GatekeeperInstanceProvider.setGatekeeperLocator(locator);
		GatekeeperImp.INSTANCE.setUserPickerProvider(userPickerProvider);
	}

	private String tryToGetInitParameter(String parameterName) {
		throwErrorIfKeyIsMissingFromInitInfo(parameterName);
		return initInfo.get(parameterName);
	}

	private void throwErrorIfKeyIsMissingFromInitInfo(String key) {
		if (!initInfo.containsKey(key)) {
			throw new GatekeeperInitializationException("InitInfo must contain " + key);
		}
	}

	private <T extends Object> T getImplementationThrowErrorIfNoneOrMoreThanOne(
			Iterable<T> implementations, String implementationClassName) {
		T implementation = null;
		int noOfImplementationsFound = 0;
		for (T currentImplementation : implementations) {
			noOfImplementationsFound++;
			implementation = currentImplementation;
		}
		throwErrorIfNone(noOfImplementationsFound, implementationClassName);
		throwErrorIfMoreThanOne(noOfImplementationsFound, implementationClassName);
		return implementation;
	}

	private void throwErrorIfNone(int noOfImplementationsFound, String implementationClassName) {
		if (noOfImplementationsFound == 0) {
			throw new GatekeeperInitializationException(
					"No implementations found for " + implementationClassName);
		}
	}

	private void throwErrorIfMoreThanOne(int noOfImplementationsFound,
			String implementationClassName) {
		if (noOfImplementationsFound > 1) {
			throw new GatekeeperInitializationException(
					"More than one implementation found for " + implementationClassName);
		}
	}
}
