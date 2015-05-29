package com.archer.pm.web;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

import com.archer.pm.domain.db.Poll;
import com.archer.pm.domain.db.Report;
import com.archer.pm.domain.db.User;
import com.archer.pm.domain.repo.PollRepo;
import com.archer.pm.domain.repo.ReportRepo;
import com.archer.pm.domain.repo.UserRepo;

/**
 * A central place to register application converters and formatters.
 */
@Configurable
public class ApplicationConversionServiceFactoryBean extends
		FormattingConversionServiceFactoryBean {

	@Autowired
	PollRepo pollRepo;

	@Autowired
	ReportRepo reportRepo;

	@Autowired
	UserRepo userRepo;

	@Override
	protected void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// Register application converters and formatters
	}

	public Converter<Poll, String> getPollToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<com.archer.pm.domain.db.Poll, java.lang.String>() {
			public String convert(Poll poll) {
				return new StringBuilder().append(poll.getPrivateLink())
						.append(' ').append(poll.getQuestion()).append(' ')
						.append(poll.getUserGuid()).append(' ')
						.append(poll.getStatus()).toString();
			}
		};
	}

	public Converter<String, Poll> getIdToPollConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, com.archer.pm.domain.db.Poll>() {
			public com.archer.pm.domain.db.Poll convert(java.lang.String id) {
				return pollRepo.findOne(id);
			}
		};
	}

	public Converter<Report, String> getReportToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<com.archer.pm.domain.db.Report, java.lang.String>() {
			public String convert(Report report) {
				return new StringBuilder().append(report.getReporterGuid())
						.append(' ').append(report.getPollGuid()).append(' ')
						.append(report.getReportReason()).append(' ')
						.append(report.getReportTime()).toString();
			}
		};
	}

	public Converter<BigInteger, Report> getIdToReportConverter() {
		return new org.springframework.core.convert.converter.Converter<java.math.BigInteger, com.archer.pm.domain.db.Report>() {
			public com.archer.pm.domain.db.Report convert(
					java.math.BigInteger id) {
				return reportRepo.findOne(id);
			}
		};
	}

	public Converter<String, Report> getStringToReportConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, com.archer.pm.domain.db.Report>() {
			public com.archer.pm.domain.db.Report convert(String id) {
				return getObject()
						.convert(getObject().convert(id, BigInteger.class),
								Report.class);
			}
		};
	}

	public Converter<User, String> getUserToStringConverter() {
		return new org.springframework.core.convert.converter.Converter<com.archer.pm.domain.db.User, java.lang.String>() {
			public String convert(User user) {
				return new StringBuilder().append(user.getGuid()).append(' ')
						.append(user.getUserName()).append(' ')
						.append(user.getPassWord()).append(' ')
						.append(user.getPoints()).toString();
			}
		};
	}

	public Converter<BigInteger, User> getIdToUserConverter() {
		return new org.springframework.core.convert.converter.Converter<java.math.BigInteger, com.archer.pm.domain.db.User>() {
			public com.archer.pm.domain.db.User convert(java.math.BigInteger id) {
				return userRepo.findOne(id);
			}
		};
	}

	public Converter<String, User> getStringToUserConverter() {
		return new org.springframework.core.convert.converter.Converter<java.lang.String, com.archer.pm.domain.db.User>() {
			public com.archer.pm.domain.db.User convert(String id) {
				return getObject().convert(
						getObject().convert(id, BigInteger.class), User.class);
			}
		};
	}

	public void installLabelConverters(FormatterRegistry registry) {
		registry.addConverter(getPollToStringConverter());
		registry.addConverter(getIdToPollConverter());
		registry.addConverter(getReportToStringConverter());
		registry.addConverter(getIdToReportConverter());
		registry.addConverter(getStringToReportConverter());
		registry.addConverter(getUserToStringConverter());
		registry.addConverter(getIdToUserConverter());
		registry.addConverter(getStringToUserConverter());
	}

	public void afterPropertiesSet() {
		super.afterPropertiesSet();
		installLabelConverters(getObject());
	}

}
