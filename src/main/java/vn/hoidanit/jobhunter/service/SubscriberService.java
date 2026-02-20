package vn.hoidanit.jobhunter.service;

import vn.hoidanit.jobhunter.domain.Job;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.domain.response.email.ResEmailJob;
import vn.hoidanit.jobhunter.utils.error.EmailInvalidException;
import vn.hoidanit.jobhunter.utils.error.IdInvalidException;

public interface SubscriberService {
    Subscriber handleCreateSubscriber(Subscriber subscriber) throws EmailInvalidException, IdInvalidException;

    Subscriber fetchSubscriberById(long id);

    Subscriber handleUpdateSubscriber(Subscriber subscriberReq, Subscriber subscriberDb) throws IdInvalidException;

    void sendSubscribersEmailJobs();

    ResEmailJob convertJobToSendEmail(Job job);
}
