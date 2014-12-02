package org.spanna.permissions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.spanna.Spanna;
import org.spanna.component.Component;

/**
 * Base Permissible for use in any Permissible object via proxy or extension
 */
public class PermissibleBase implements Permissible {
    private ServerOperator opable = null;
    private Permissible parent = this;
    private final List<PermissionAttachment> attachments = new LinkedList<PermissionAttachment>();
    private final Map<String, PermissionAttachmentInfo> permissions = new HashMap<String, PermissionAttachmentInfo>();

    public PermissibleBase(ServerOperator opable) {
        this.opable = opable;

        if (opable instanceof Permissible) {
            this.parent = (Permissible) opable;
        }

        recalculatePermissions();
    }

    public boolean isOp() {
        if (opable == null) {
            return false;
        } else {
            return opable.isOp();
        }
    }

    public void setOp(boolean value) {
        if (opable == null) {
            throw new UnsupportedOperationException("[ERROR] Cannot change op value as no ServerOperator is set");
        } else {
            opable.setOp(value);
        }
    }

    public boolean isPermissionSet(String name) {
        if (name == null) {
            throw new IllegalArgumentException("[ERROR] Permission name cannot be null");
        }

        return permissions.containsKey(name.toLowerCase());
    }

    public boolean isPermissionSet(Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("[ERROR] Permission cannot be null");
        }

        return isPermissionSet(perm.getName());
    }

    public boolean hasPermission(String inName) {
        if (inName == null) {
            throw new IllegalArgumentException("[ERROR] Permission name cannot be null");
        }

        String name = inName.toLowerCase();

        if (isPermissionSet(name)) {
            return permissions.get(name).getValue();
        } else {
            Permission perm = Spanna.getServer().getComponentManager().getPermission(name);

            if (perm != null) {
                return perm.getDefault().getValue(isOp());
            } else {
                return Permission.DEFAULT_PERMISSION.getValue(isOp());
            }
        }
    }

    public boolean hasPermission(Permission perm) {
        if (perm == null) {
            throw new IllegalArgumentException("[ERROR] Permission cannot be null");
        }

        String name = perm.getName().toLowerCase();

        if (isPermissionSet(name)) {
            return permissions.get(name).getValue();
        }
        return perm.getDefault().getValue(isOp());
    }

    public PermissionAttachment addAttachment(Component component, String name, boolean value) {
        if (name == null) {
            throw new IllegalArgumentException("[ERROR] Permission name cannot be null");
        } else if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        } else if (!component.isEnabled()) {
            throw new IllegalArgumentException("Component " + component.getDescription().getFullName() + " is disabled!");
        }

        PermissionAttachment result = addAttachment(component);
        result.setPermission(name, value);

        recalculatePermissions();

        return result;
    }

    public PermissionAttachment addAttachment(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        } else if (!component.isEnabled()) {
            throw new IllegalArgumentException("[ERROR] Component " + component.getDescription().getFullName() + " is disabled!");
        }

        PermissionAttachment result = new PermissionAttachment(component, parent);

        attachments.add(result);
        recalculatePermissions();

        return result;
    }

    public void removeAttachment(PermissionAttachment attachment) {
        if (attachment == null) {
            throw new IllegalArgumentException("[ERROR] Attachment cannot be null");
        }

        if (attachments.contains(attachment)) {
            attachments.remove(attachment);
            PermissionRemovedExecutor ex = attachment.getRemovalCallback();

            if (ex != null) {
                ex.attachmentRemoved(attachment);
            }

            recalculatePermissions();
        } else {
            throw new IllegalArgumentException("[ERROR] Given attachment is not part of Permissible object " + parent);
        }
    }

    public void recalculatePermissions() {
        clearPermissions();
        Set<Permission> defaults = Spanna.getServer().getComponentManager().getDefaultPermissions(isOp());
        Spanna.getServer().getComponentManager().subscribeToDefaultPerms(isOp(), parent);

        for (Permission perm : defaults) {
            String name = perm.getName().toLowerCase();
            permissions.put(name, new PermissionAttachmentInfo(parent, name, null, true));
            Spanna.getServer().getComponentManager().subscribeToPermission(name, parent);
            calculateChildPermissions(perm.getChildren(), false, null);
        }

        for (PermissionAttachment attachment : attachments) {
            calculateChildPermissions(attachment.getPermissions(), false, attachment);
        }
    }

    public synchronized void clearPermissions() {
        Set<String> perms = permissions.keySet();

        for (String name : perms) {
            Spanna.getServer().getComponentManager().unsubscribeFromPermission(name, parent);
        }

        Spanna.getServer().getComponentManager().unsubscribeFromDefaultPerms(false, parent);
        Spanna.getServer().getComponentManager().unsubscribeFromDefaultPerms(true, parent);

        permissions.clear();
    }

    private void calculateChildPermissions(Map<String, Boolean> children, boolean invert, PermissionAttachment attachment) {
        Set<String> keys = children.keySet();

        for (String name : keys) {
            Permission perm = Spanna.getServer().getPluginManager().getPermission(name);
            boolean value = children.get(name) ^ invert;
            String lname = name.toLowerCase();

            permissions.put(lname, new PermissionAttachmentInfo(parent, lname, attachment, value));
            Spanna.getServer().getComponentManager().subscribeToPermission(name, parent);

            if (perm != null) {
                calculateChildPermissions(perm.getChildren(), !value, attachment);
            }
        }
    }

    public PermissionAttachment addAttachment(Component component, String name, boolean value, int ticks) {
        if (name == null) {
            throw new IllegalArgumentException("[ERROR] Permission name cannot be null");
        } else if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        } else if (!component.isEnabled()) {
            throw new IllegalArgumentException("[ERROR] Component " + component.getDescription().getFullName() + " is disabled!");
        }

        PermissionAttachment result = addAttachment(component, ticks);

        if (result != null) {
            result.setPermission(name, value);
        }

        return result;
    }

    public PermissionAttachment addAttachment(Component component, int ticks) {
        if (component == null) {
            throw new IllegalArgumentException("[ERROR] Component cannot be null");
        } else if (!component.isEnabled()) {
            throw new IllegalArgumentException("[ERROR] Component " + component.getDescription().getFullName() + " is disabled!");
        }

        PermissionAttachment result = addAttachment(component);

        if (Spanna.getServer().getScheduler().scheduleSyncDelayedTask(component, new RemoveAttachmentRunnable(result), ticks) == -1) {
            Spanna.getServer().getLogger().log(Level.WARNING, "[ERROR] Could not add PermissionAttachment to " + parent + " for component " + component.getDescription().getFullName() + ": Scheduler returned -1");
            result.remove();
            return null;
        } else {
            return result;
        }
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return new HashSet<PermissionAttachmentInfo>(permissions.values());
    }

    private class RemoveAttachmentRunnable implements Runnable {
        private PermissionAttachment attachment;

        public RemoveAttachmentRunnable(PermissionAttachment attachment) {
            this.attachment = attachment;
        }

        public void run() {
            attachment.remove();
        }
    }
}
