
CREATE DATABASE `p2p` /*!40100 DEFAULT CHARACTER SET utf8 */;


CREATE TABLE `task` (
  `task_id` varchar(255) NOT NULL,
  `biz_id` varchar(255) DEFAULT NULL,
  `cdn_status` int(11) DEFAULT NULL,
  `cur_ip` varchar(255) DEFAULT NULL,
  `dfdaemon` bit(1) NOT NULL,
  `file_length` bigint(20) DEFAULT NULL,
  `headers` tinyblob,
  `http_file_len` bigint(20) DEFAULT NULL,
  `md5` varchar(255) DEFAULT NULL,
  `not_reachable` bit(1) NOT NULL,
  `peer_ips` varchar(255) DEFAULT NULL,
  `piece_md5` tinyblob,
  `piece_size` int(11) DEFAULT NULL,
  `piece_total` int(11) DEFAULT NULL,
  `real_md5` varchar(255) DEFAULT NULL,
  `source_url` varchar(255) DEFAULT NULL,
  `task_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`task_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `peer_info` (
  `cid` varchar(255) NOT NULL,
  `host_name` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `peer_task` (
  `cid` varchar(255) NOT NULL,
  `path` varchar(255) DEFAULT NULL,
  `piece_size` int(11) DEFAULT NULL,
  `port` int(11) NOT NULL,
  `status` int(11) DEFAULT NULL,
  `task_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`cid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
