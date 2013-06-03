package elka.clouddir.generator;

import elka.clouddir.server.model.User;
import elka.clouddir.server.model.UserGroup;
import elka.clouddir.server.model.UserGroupList;
import elka.clouddir.server.model.UserList;

public class Run {

	/**
	 * U�yte do wygenerowania plik�w z u�ytkownikami i grupami
	 */
	public static void main(String[] args) {
		run();

	}

	private static void run() {
		UserGroup elka = new UserGroup("elka", "ServerFiles/elka");
		UserGroup phds = new UserGroup("phds", "ServerFiles/phds");
		UserGroup admins = new UserGroup("admins", "ServerFiles/admins");
		UserGroup hr = new UserGroup("hr", "ServerFiles/hr");
		UserGroup dof = new UserGroup("deans-office", "ServerFiles/deans-office");
		
		User luk = new User("lpielasz", false, elka, "mango");
		User raf = new User("mtoporow", false, elka, "awokado");
		User bog = new User("bshkol", false, elka, "kiwi");
		User jan = new User("jkoper", false, phds, "granat");
		
		UserList users = new UserList();
		UserGroupList groups = new UserGroupList();
		
		users.getUsers().add(luk);
		users.getUsers().add(raf);
		users.getUsers().add(bog);
		users.getUsers().add(jan);
		
		groups.getGroups().add(elka);
		groups.getGroups().add(phds);
		groups.getGroups().add(admins);
		groups.getGroups().add(hr);
		groups.getGroups().add(dof);
		
		users.pushToFile();
		groups.pushToFile();
		
		UserList newusers = new UserList();
		UserGroupList newgroups = new UserGroupList();
		
		newusers.pullFromFile();
		newgroups.pullFromFile();
		
		System.out.println("#users");
		System.out.println(newusers.getUsers().get(0).getName());
		System.out.println(newusers.getUsers().get(1).getName());
		System.out.println(newusers.getUsers().get(2).getName());
		System.out.println(newusers.getUsers().get(3).getName());
		//System.out.println(newusers.getUsers().get(3).getUserGroup().getName());

		
		System.out.println();
		System.out.println("#groups");
		
		System.out.println(newgroups.getGroups().get(0).getName());
		System.out.println(newgroups.getGroups().get(1).getName());
		System.out.println(newgroups.getGroups().get(2).getName());
		System.out.println(newgroups.getGroups().get(3).getName());
		System.out.println(newgroups.getGroups().get(4).getName());
	}
}
