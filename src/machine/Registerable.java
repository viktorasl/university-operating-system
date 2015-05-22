package machine;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class Registerable {
	
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}
	
}
