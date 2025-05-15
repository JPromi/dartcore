package com.jpromi.darts.backend.services;

import com.jpromi.darts.backend.models.EmailObject;

public interface MailService {
    void send(EmailObject mail);
}
