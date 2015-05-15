package resources;

import java.util.ArrayList;
import java.util.List;

import models.TElement;
import models.TProcess;
import models.TResource;
import models.TWaitingProc;

public class Idle extends TResource {
	static int rID = 0;
	static int rAmount = 0;
	static TProcess rCreator = null;
	static boolean rReusable = true;
	static List<TElement> rAccElem = new ArrayList<TElement>();
	static List<TWaitingProc> rWaitProcList = new ArrayList<TWaitingProc>();
}
