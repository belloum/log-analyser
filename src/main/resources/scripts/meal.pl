#!/usr/bin/perl
# Implements the following auto:
# "X becomes true and Y becomes true
#    during meal time"

use Getopt::Std;
our %opts = ();

getopts('m:b:e:1:2:trf', \%opts) or die "available options:
  -m name: meal name (e.g. lunch)
  -b hh:mm[:ss]: begin slot time
  -e hh:mm[:ss]: end slot time
  -1 pattern: match the name of primary marker
  -2 pattern: match the name of secondary marker
  -t: print trace of all markers
  -r: report real timestamp of a singleton marker, instead the end slot time
  -f: print input file name on each line";

my $meal = $opts{'m'} || "meal";
my %lastval = (); # last value of each sensor
my %lastts = (); # last timestamp of each sensor value
my $lastday; # calendar day of the last log line
my @marker1ts; # timestamps primary markers wihtin slot
my @marker2ts; # timestamps of secondary markers wihtin slot
my $begints = &timestamp($opts{'b'} || "05:00");
my $endts = &timestamp($opts{'e'} || "23:59");
my $marker1 = $opts{'1'} || "EMeter_.*";
my $marker2 = $opts{'2'} || "ContactS_(Fridge|Cupboard)";

while(<>) {
  if(my ($date, undef, $val, $loc, $s) =
  /\{"date":"([^"]*)"(,"(?:id|state)":"[^"]*")?,"value":([\d.]*),"device":\{"location":"([^"]*)","id":"([^"]*)"(,"type":"[^"]*")?\},"timestamp":\d*\}/
  ) {
    my $ts = &timestamp($date);
    if($s =~ /^EMeter_/) { # normalize value
      if($val >= 20) { $val = 1; }
      else { $val = 0; }
    }
    my ($day) = &datetime($date) =~ /^(\d\d\d\d-\d\d-\d\d ...)/;
    if(defined($lastday) && $day ne $lastday) { # at the end of the day
      &printscore() if $opts{'m'};
      @marker1ts = ();
      @marker2ts = ();
    }
    if(&inslot($ts)
      ) {
      if($s =~ /^$marker1$/o && $val == 1
        #&& ($s != /^EMeter_/ || $lastval{$s} == 0)
        ) {
        push @marker1ts, $ts;
        if($opts{'t'}) {
            print "    @{[&datetime($date)]}; $ts; M1; $s\n";
        }
      } elsif($s =~ /^$marker2$/o && $val == 1
        #&& ($s != /^EMeter_/ || $lastval{$s} == 0) 
        ) {
        push @marker2ts, $ts;
        if($opts{'t'}) {
            print "    @{[&datetime($date)]}; $ts; M2; $s\n";
        }
      }
    }
    $lastval{$s} = $val;
    $lastts{$s} = $ts;
    $lastday = $day;
  }
}
&printscore() if $opts{'m'};

sub timestamp {
  my ($date) = @_;
  my ($hh, $mm, undef, $ss) =
    ($date =~ /(\d\d?):(\d\d?)(:(\d\d?))?/);
  my $ts = 60 * (60 * $hh + $mm) + $ss;
  return $ts;
}

sub time {
  use integer;
  my ($ts) = @_;
  my $hh = $ts / (60 * 60);
  $ts = $ts % (60 * 60);
  my $mm = $ts / 60;
  $ts = $ts % 60;
  my $ss = $ts;
  return sprintf("%02d:%02d:%02d", $hh, $mm, $ss);
}

sub period {
  my ($t1, $t2) = @_;
  if($t1 <= $t2) { return $t2 - $t1; }
  else { return (24 * 60 * 60) - $t1 + $t2; }
}

sub inslot {
  my ($t) = @_;
  return &period($begints, $t) <= &period($begints, $endts);
}

sub score {
  my ($m1ts, $m2ts) = @_;
  if($marker1 =~ /^EMeter_/) {
    return ($m1ts > 0? 0.8: 0) + ($m2ts > 0? 0.2: 0);
  } else {
    return $m1ts > 0?
      ($m2ts > 0? 1: 0.8):
      ($m2ts > 0? 0.7: 0);
  }
}

sub printscore {
  if($opts{'f'}) {
    print "$ARGV; $lastday";
  } else {
    print "$lastday";
  }
  my $ts; # timestamp when the meal was detected
  $ts = $#marker1ts >= 0 && $#marker2ts >= 0 || $opts{'r'}?
    # max(m1ts,m2ts)
    ($marker1ts[0] >= $marker2ts[0]? $marker1ts[0]: $marker2ts[0]):
    #$#marker1ts >= 0? marker1ts[0]:
    #$#marker2ts >= 0? $marker2ts[0]:
    $endts;
  print "; @{[&time($ts)]}; $ts" .
      "; $meal; @{[&score($#marker1ts + 1, $#marker2ts + 1)]}" .
      "; M1[@{[$#marker1ts + 1]}] at @{[&time($marker1ts[0])]}," .
      " M2[@{[$#marker2ts + 1]}] at @{[&time($marker2ts[0])]}\n";
}

sub datetime {
  my ($date) = @_;
  my %mon2mm = ('Jan' => 1, 'Feb' => 2, 'Mar' => 3, 'Apr' => 4,
    'May' => 5, 'Jun' => 6, 'Jul' => 7, 'Aug' => 8,
    'Sep' => 9, 'Oct' => 10, 'Nov' => 11, 'Dec' => 12);
  ($day, $mon, $nn, $time, $zone, $year) =
    $date =~ /(...) (...) (\d\d) (\d\d:\d\d:\d\d) (\S+) (\d\d\d\d)/;
  my $mm = sprintf("%02d", $mon2mm{$mon});
  return "$year-$mm-$nn $day; $time";
}
