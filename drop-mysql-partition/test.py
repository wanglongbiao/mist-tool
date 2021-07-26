
z = 8
y = 84
x = 140
while z <= 18:
    print('./run_creator_parallel_upload3.sh ', str(z), str(y * (2**(z-8))), str(x * (2**(z-8))))
    z = z + 1
