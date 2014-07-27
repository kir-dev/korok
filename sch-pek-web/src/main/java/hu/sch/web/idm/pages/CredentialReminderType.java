package hu.sch.web.idm.pages;

public enum CredentialReminderType {
        USERNAME, PASSWORD;

        public String lowercase() {
            return name().toLowerCase();
        }
}
