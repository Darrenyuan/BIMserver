package org.bimserver.database.actions;

import org.bimserver.database.BimDatabaseException;
import org.bimserver.database.BimDatabaseSession;
import org.bimserver.database.BimDeadlockException;
import org.bimserver.database.store.ObjectState;
import org.bimserver.database.store.User;
import org.bimserver.database.store.UserType;
import org.bimserver.database.store.log.AccessMethod;
import org.bimserver.shared.UserException;

public class DeleteUserDatabaseAction extends BimDatabaseAction<Boolean> {

	private final long actingUoid;
	private final long uoid;

	public DeleteUserDatabaseAction(BimDatabaseSession bimDatabaseSession, AccessMethod accessMethod, long actingUoid, long uoid) {
		super(bimDatabaseSession, accessMethod);
		this.actingUoid = actingUoid;
		this.uoid = uoid;
	}

	@Override
	public Boolean execute() throws UserException, BimDatabaseException, BimDeadlockException {
		User actingUser = getUserByUoid(actingUoid);
		if (actingUser.getUserType() != UserType.ADMIN) {
			throw new UserException("Only administrators can delete users accounts");
		}
		final User user = getUserByUoid(uoid);
		if (user.getUserType() == UserType.ADMIN || user.getUserType() == UserType.ANONYMOUS) {
			throw new UserException("Cannot delete this user");
		}
		user.setState(ObjectState.DELETED);
		getDatabaseSession().store(user);
		return true;
	}
}